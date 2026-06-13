<?php
// peliculas.php - API CRUD de películas

require_once 'config.php';

$metodo = $_SERVER['REQUEST_METHOD'];
$accion = isset($_GET['action']) ? $_GET['action'] : '';
$id_usuario = isset($_GET['id_usuario']) ? intval($_GET['id_usuario']) : 0;

// Validar que el usuario esté autenticado
if ($id_usuario === 0) {
    responder(['error' => 'Usuario no autenticado'], 401);
}

switch ($metodo) {
    case 'GET':
        if ($accion === 'listar') {
            listar_peliculas($id_usuario);
        } elseif ($accion === 'obtener') {
            obtener_pelicula($id_usuario);
        } else {
            responder(['error' => 'Acción no válida'], 400);
        }
        break;
    case 'POST':
        insertar_pelicula($id_usuario);
        break;
    case 'PUT':
        actualizar_pelicula($id_usuario);
        break;
    case 'DELETE':
        eliminar_pelicula($id_usuario);
        break;
    default:
        responder(['error' => 'Método no permitido'], 405);
}

function listar_peliculas($id_usuario) {
    global $conn;
    
    $filtro = '';
    if (isset($_GET['estado']) && !empty($_GET['estado'])) {
        $estado = $conn->real_escape_string($_GET['estado']);
        $filtro = " AND estado = '$estado'";
    }
    
    $sql = "SELECT id_pelicula, titulo, director, genero, año, descripcion, 
            puntuacion, estado, fecha_agregada FROM peliculas 
            WHERE id_usuario = $id_usuario $filtro 
            ORDER BY fecha_agregada DESC";
    
    $resultado = $conn->query($sql);
    
    if (!$resultado) {
        responder(['error' => 'Error en la consulta'], 500);
    }
    
    $peliculas = [];
    while ($fila = $resultado->fetch_assoc()) {
        $peliculas[] = $fila;
    }
    
    responder([
        'error' => false,
        'total' => count($peliculas),
        'peliculas' => $peliculas
    ]);
}

function obtener_pelicula($id_usuario) {
    global $conn;
    
    $id_pelicula = isset($_GET['id_pelicula']) ? intval($_GET['id_pelicula']) : 0;
    
    if ($id_pelicula === 0) {
        responder(['error' => 'ID de película no válido'], 400);
    }
    
    $sql = "SELECT * FROM peliculas WHERE id_pelicula = $id_pelicula AND id_usuario = $id_usuario";
    $resultado = $conn->query($sql);
    
    if ($resultado->num_rows === 0) {
        responder(['error' => 'Película no encontrada'], 404);
    }
    
    responder([
        'error' => false,
        'pelicula' => $resultado->fetch_assoc()
    ]);
}

function insertar_pelicula($id_usuario) {
    global $conn;
    
    $datos = json_decode(file_get_contents('php://input'), true);
    
    // Validar campos requeridos
    if (empty($datos['titulo'])) {
        responder(['error' => 'El título es requerido'], 400);
    }
    
    $titulo = $conn->real_escape_string($datos['titulo']);
    $director = $conn->real_escape_string($datos['director'] ?? '');
    $genero = $conn->real_escape_string($datos['genero'] ?? '');
    $año = isset($datos['año']) ? intval($datos['año']) : 0;
    $descripcion = $conn->real_escape_string($datos['descripcion'] ?? '');
    $estado = $conn->real_escape_string($datos['estado'] ?? 'Por ver');
    $puntuacion = isset($datos['puntuacion']) ? intval($datos['puntuacion']) : 0;
    
    $sql = "INSERT INTO peliculas (id_usuario, titulo, director, genero, año, 
            descripcion, estado, puntuacion) 
            VALUES ($id_usuario, '$titulo', '$director', '$genero', $año, 
            '$descripcion', '$estado', $puntuacion)";
    
    if ($conn->query($sql)) {
        responder([
            'error' => false,
            'mensaje' => 'Película agregada correctamente',
            'id_pelicula' => $conn->insert_id
        ], 201);
    } else {
        responder(['error' => 'Error al insertar: ' . $conn->error], 500);
    }
}

function actualizar_pelicula($id_usuario) {
    global $conn;
    
    $datos = json_decode(file_get_contents('php://input'), true);
    $id_pelicula = isset($datos['id_pelicula']) ? intval($datos['id_pelicula']) : 0;
    
    if ($id_pelicula === 0) {
        responder(['error' => 'ID de película requerido'], 400);
    }
    
    // Verificar que la película pertenece al usuario
    $verificar = $conn->query("SELECT id_pelicula FROM peliculas WHERE id_pelicula = $id_pelicula AND id_usuario = $id_usuario");
    if ($verificar->num_rows === 0) {
        responder(['error' => 'Película no encontrada o no tienes permiso'], 403);
    }
    
    // Construir consulta de actualización dinámica
    $actualizaciones = [];
    
    if (isset($datos['titulo'])) {
        $titulo = $conn->real_escape_string($datos['titulo']);
        $actualizaciones[] = "titulo = '$titulo'";
    }
    if (isset($datos['director'])) {
        $director = $conn->real_escape_string($datos['director']);
        $actualizaciones[] = "director = '$director'";
    }
    if (isset($datos['genero'])) {
        $genero = $conn->real_escape_string($datos['genero']);
        $actualizaciones[] = "genero = '$genero'";
    }
    if (isset($datos['año'])) {
        $año = intval($datos['año']);
        $actualizaciones[] = "año = $año";
    }
    if (isset($datos['descripcion'])) {
        $descripcion = $conn->real_escape_string($datos['descripcion']);
        $actualizaciones[] = "descripcion = '$descripcion'";
    }
    if (isset($datos['estado'])) {
        $estado = $conn->real_escape_string($datos['estado']);
        $actualizaciones[] = "estado = '$estado'";
    }
    if (isset($datos['puntuacion'])) {
        $puntuacion = intval($datos['puntuacion']);
        $actualizaciones[] = "puntuacion = $puntuacion";
    }
    
    if (empty($actualizaciones)) {
        responder(['error' => 'No hay datos para actualizar'], 400);
    }
    
    $sql = "UPDATE peliculas SET " . implode(', ', $actualizaciones) . 
           " WHERE id_pelicula = $id_pelicula";
    
    if ($conn->query($sql)) {
        responder([
            'error' => false,
            'mensaje' => 'Película actualizada correctamente'
        ]);
    } else {
        responder(['error' => 'Error al actualizar: ' . $conn->error], 500);
    }
}

function eliminar_pelicula($id_usuario) {
    global $conn;
    
    $id_pelicula = isset($_GET['id_pelicula']) ? intval($_GET['id_pelicula']) : 0;
    
    if ($id_pelicula === 0) {
        responder(['error' => 'ID de película requerido'], 400);
    }
    
    // Verificar que la película pertenece al usuario
    $verificar = $conn->query("SELECT id_pelicula FROM peliculas WHERE id_pelicula = $id_pelicula AND id_usuario = $id_usuario");
    if ($verificar->num_rows === 0) {
        responder(['error' => 'Película no encontrada o no tienes permiso'], 403);
    }
    
    $sql = "DELETE FROM peliculas WHERE id_pelicula = $id_pelicula";
    
    if ($conn->query($sql)) {
        responder([
            'error' => false,
            'mensaje' => 'Película eliminada correctamente'
        ]);
    } else {
        responder(['error' => 'Error al eliminar: ' . $conn->error], 500);
    }
}
?>