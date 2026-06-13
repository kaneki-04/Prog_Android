<?php
// config.php - Configuración de la base de datos

header('Content-Type: application/json');

// Datos de conexión - CAMBIAR SEGÚN TU SERVIDOR
define('DB_HOST', 'localhost');
define('DB_USER', 'root');
define('DB_PASS', '');
define('DB_NAME', 'gestor_peliculas');
define('API_KEY', 'tu_clave_secreta_aqui');

// Crear conexión
$conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

// Verificar conexión
if ($conn->connect_error) {
    http_response_code(500);
    die(json_encode(['error' => 'Error de conexión: ' . $conn->connect_error]));
}

// Establecer charset
$conn->set_charset('utf8mb4');

// Función para responder en JSON
function responder($datos, $codigo = 200) {
    http_response_code($codigo);
    echo json_encode($datos);
    exit;
}

// Función para validar token
function validar_token() {
    $headers = getallheaders();
    $token = isset($headers['Authorization']) ? str_replace('Bearer ', '', $headers['Authorization']) : '';
    
    if (empty($token)) {
        responder(['error' => 'Token no proporcionado'], 401);
    }
    
    // Aquí validarías el token JWT en producción
    // Por ahora hacemos validación básica
    return true;
}
?>