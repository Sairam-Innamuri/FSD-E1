<?php
require_once '../includes/auth.php';
require_once '../config/database.php';
header('Content-Type: application/json');

if (!isLoggedIn()) {
    echo json_encode(['error' => 'Unauthorized']);
    exit;
}

// Supported quiz categories
$allowedCategories = ['movies', 'indian_capitals', 'famous_places', 'general_knowledge', 'sports'];

$category = $_GET['category'] ?? 'general_knowledge';
if (!in_array($category, $allowedCategories, true)) {
    $category = 'general_knowledge';
}

$pdo = getDBConnection();
$stmt = $pdo->prepare("SELECT id, question_text, option_a, option_b, option_c, option_d FROM questions WHERE category = ? ORDER BY RAND() LIMIT 10");
$stmt->execute([$category]);
$questions = $stmt->fetchAll(PDO::FETCH_ASSOC);

// Don't send correct_answer to client - we'll verify on submit
foreach ($questions as &$q) {
    $q['options'] = [
        ['key' => 'A', 'text' => $q['option_a']],
        ['key' => 'B', 'text' => $q['option_b']],
        ['key' => 'C', 'text' => $q['option_c']],
        ['key' => 'D', 'text' => $q['option_d']],
    ];
    unset($q['option_a'], $q['option_b'], $q['option_c'], $q['option_d']);
}

echo json_encode([
    'category' => $category,
    'questions' => $questions
]);
