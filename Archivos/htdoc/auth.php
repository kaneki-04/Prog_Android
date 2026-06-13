<?php
// auth.php - Servicio de autenticación

require_once 'config.php';

$metodo = $_SERVER['REQUEST_METHOD'];
$accion = isset($_GET['action']) ? $_GET['action'] : '';

if ($metodo === 'POST') {
    $datos = json_decode(file_get_contents('php://input'), true);
    
    switch ($accion) {
        case 'registro':
            registrar_usuario($datos);
            break;
        case 'login':
            iniciar_sesion($datos);
            break;
        case 'google_login':
            login_google($datos);
            break;
        default:
            responder(['error' => 'Acción no válida'], 400);
    }
}

function registrar_usuario($datos) {
    global $conn;
    
    if (empty($datos['email']) || empty($datos['password']) || empty($datos['nombre'])) {
        responder(['error' => 'Campos requeridos incompletos'], 400);
    }
    
    $email = $conn->real_escape_string(trim($datos['email']));
    $password = password_hash($datos['password'], PASSWORD_BCRYPT);
    $nombre = $conn->real_escape_string(trim($datos['nombre']));
    
    // Verificar si el email ya existe
    $resultado = $conn->query("SELECT id_usuario FROM usuarios WHERE email = '$email'");
    if ($resultado->num_rows > 0) {
        responder(['error' => 'El email ya está registrado'], 400);
    }
    
    $sql = "INSERT INTO usuarios (email, password, nombre) VALUES ('$email', '$password', '$nombre')";
    
    if ($conn->query($sql)) {
        $id_usuario = $conn->insert_id;
        $token = generar_token($id_usuario);
        responder([
            'error' => false,
            'mensaje' => 'Usuario registrado correctamente',
            'id_usuario' => $id_usuario,
            'nombre' => $nombre,
            'email' => $email,
            'token' => $token
        ], 201);
    } else {
        responder(['error' => 'Error en el registro: ' . $conn->error], 500);
    }
}

function iniciar_sesion($datos) {
    global $conn;
    
    if (empty($datos['email']) || empty($datos['password'])) {
        responder(['error' => 'Email y contraseña requeridos'], 400);
    }
    
    $email = $conn->real_escape_string(trim($datos['email']));
    $sql = "SELECT id_usuario, nombre, password, email FROM usuarios WHERE email = '$email'";
    $resultado = $conn->query($sql);
    
    if ($resultado->num_rows === 0) {
        responder(['error' => 'Email o contraseña incorrectos'], 401);
    }
    
    $usuario = $resultado->fetch_assoc();
    
    if (!password_verify($datos['password'], $usuario['password'])) {
        responder(['error' => 'Email o contraseña incorrectos'], 401);
    }
    
    $token = generar_token($usuario['id_usuario']);
    
    responder([
        'error' => false,
        'mensaje' => 'Sesión iniciada correctamente',
        'id_usuario' => $usuario['id_usuario'],
        'nombre' => $usuario['nombre'],
        'email' => $usuario['email'],
        'token' => $token
    ]);
}

function login_google($datos) {
    global $conn;
    
    // En producción, verificarías el token de Google aquí
    // Por ahora, simulamos el login
    
    if (empty($datos['google_id']) || empty($datos['nombre']) || empty($datos['email'])) {
        responder(['error' => 'Datos de Google incompletos'], 400);
    }
    
    $google_id = $conn->real_escape_string($datos['google_id']);
    $email = $conn->real_escape_string($datos['email']);
    $nombre = $conn->real_escape_string($datos['nombre']);
    
    // Buscar usuario existente
    $resultado = $conn->query("SELECT id_usuario FROM usuarios WHERE google_id = '$google_id' OR email = '$email'");
    
    if ($resultado->num_rows > 0) {
        $usuario = $resultado->fetch_assoc();
        $id_usuario = $usuario['id_usuario'];
    } else {
        // Crear nuevo usuario
        $sql = "INSERT INTO usuarios (email, nombre, google_id, password) 
                VALUES ('$email', '$nombre', '$google_id', 'google_auth')";
        if (!$conn->query($sql)) {
            responder(['error' => 'Error al crear usuario'], 500);
        }
        $id_usuario = $conn->insert_id;
    }
    
    $token = generar_token($id_usuario);
    
    responder([
        'error' => false,
        'mensaje' => 'Login con Google exitoso',
        'id_usuario' => $id_usuario,
        'nombre' => $nombre,
        'email' => $email,
        'token' => $token
    ]);
}

function generar_token($id_usuario) {
    // Token simple (en producción usarías JWT)
    return base64_encode($id_usuario . ':' . time() . ':' . uniqid());
}
?>