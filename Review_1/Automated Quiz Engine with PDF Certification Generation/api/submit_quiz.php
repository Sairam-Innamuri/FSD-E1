<?php
require_once '../includes/auth.php';
require_once '../config/database.php';
header('Content-Type: application/json');

if (!isLoggedIn()) {
    echo json_encode(['error' => 'Unauthorized']);
    exit;
}

$input = json_decode(file_get_contents('php://input'), true);
$answers = $input['answers'] ?? [];
$timeTaken = (int)($input['time_taken'] ?? 0);
$category = $input['category'] ?? 'general_knowledge';

// Supported quiz categories
$allowedCategories = ['movies', 'indian_capitals', 'famous_places', 'general_knowledge', 'sports'];
if (!in_array($category, $allowedCategories, true)) {
    $category = 'general_knowledge';
}

// Allow skipped questions: only require that the payload is an array
if (!is_array($answers) || empty($answers)) {
    echo json_encode(['error' => 'Invalid submission']);
    exit;
}

$pdo = getDBConnection();

// Get correct answers for submitted question IDs
$ids = array_keys($answers);
$placeholders = implode(',', array_fill(0, count($ids), '?'));
$stmt = $pdo->prepare("SELECT id, correct_answer FROM questions WHERE id IN ($placeholders)");
$stmt->execute($ids);
$correctMap = [];
while ($row = $stmt->fetch()) {
    $correctMap[$row['id']] = $row['correct_answer'];
}

$score = 0;
foreach ($answers as $qId => $userAnswer) {
    if (isset($correctMap[$qId]) && strtoupper(trim($userAnswer)) === $correctMap[$qId]) {
        $score++;
    }
}

// Determine badge
if ($score >= 9) $badge = 'Gold';
elseif ($score >= 7) $badge = 'Silver';
else $badge = 'Bronze';

// Save attempt
$stmt = $pdo->prepare("INSERT INTO attempts (user_id, score, total_questions, badge, quiz_category, time_taken) VALUES (?, ?, 10, ?, ?, ?)");
$stmt->execute([$_SESSION['user_id'], $score, $badge, $category, $timeTaken]);
$attemptId = $pdo->lastInsertId();

echo json_encode([
    'success' => true,
    'attempt_id' => (int)$attemptId,
    'score' => $score,
    'total' => 10,
    'badge' => $badge,
    'time_taken' => $timeTaken
]);
