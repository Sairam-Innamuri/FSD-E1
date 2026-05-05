<?php
/**
 * Database Configuration - Automated Quiz Engine
 * If you get "Access denied", set DB_PASS to your MySQL root password.
 */
define('DB_HOST', '127.0.0.1');
define('DB_NAME', 'quiz_engine');
define('DB_USER', 'root');
define('DB_PASS', 'Sairam@7121');  // Add your MySQL password here if root has one

function getDBConnection() {
    try {
        $pdo = new PDO(
            "mysql:host=" . DB_HOST . ";dbname=" . DB_NAME . ";charset=utf8mb4",
            DB_USER,
            DB_PASS,
            [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
            ]
        );
        return $pdo;
    } catch (PDOException $e) {
        die("Database connection failed: " . $e->getMessage());
    }
}
