<?php
require_once 'includes/auth.php';
require_once 'config/database.php';
requireLogin();

$attemptId = (int)($_GET['id'] ?? 0);
$user = getUserData();

if (!$attemptId) {
    header('Location: dashboard.php');
    exit;
}

$pdo = getDBConnection();
$stmt = $pdo->prepare("SELECT * FROM attempts WHERE id = ? AND user_id = ?");
$stmt->execute([$attemptId, $user['id']]);
$attempt = $stmt->fetch();

if (!$attempt) {
    header('Location: dashboard.php');
    exit;
}

$badge = $attempt['badge'];
$score = $attempt['score'];
$total = $attempt['total_questions'];
$date = date('F j, Y', strtotime($attempt['completed_at']));

// Category label for certificate
$categoryKey = $attempt['quiz_category'] ?? 'general_knowledge';
$categoryNames = [
    'movies' => 'Movie Quiz',
    'indian_capitals' => 'Indian Capitals Quiz',
    'famous_places' => 'Famous Places in India Quiz',
    'general_knowledge' => 'General Knowledge Quiz',
    'sports' => 'Sports Quiz',
];
$categoryLabel = $categoryNames[$categoryKey] ?? 'General Knowledge Quiz';

// Helper to escape text for PDF
$escapePdfText = function (string $text): string {
    $text = str_replace(['\\', '(', ')'], ['\\\\', '\\(', '\\)'], $text);
    return $text;
};

$nameText = $user['name'] ?? 'Participant';
$title = 'CERTIFICATE OF PARTICIPATION';
$subtitle = 'Automated Quiz Engine - ' . $categoryLabel;
$line1 = 'This certificate is proudly presented to';
$line2 = 'for participating in the ' . $categoryLabel;
$lineDate = 'Completed on ' . $date;
$badgeTitle = $badge . ' Badge';
$scoreLine = 'Score: ' . $score . ' / ' . $total;

// Build a one-page PDF (A4 landscape) styled similar to the provided template
// Coordinates are in points (1/72 inch). A4 landscape ~ 842 x 595.
$content = '';

// Layout constants
$PAGE_W = 842;
$PAGE_H = 595;

// Very small centering helper (approx; avoids complex font metrics)
$approxTextWidth = function (string $text, float $fontSize, float $factor = 0.52): float {
    $len = mb_strlen($text, 'UTF-8');
    return $len * $fontSize * $factor;
};
$centerX = function (string $text, float $fontSize, float $factor = 0.52) use ($PAGE_W, $approxTextWidth): float {
    $w = $approxTextWidth($text, $fontSize, $factor);
    $x = ($PAGE_W - $w) / 2;
    return max(70, $x); // keep inside margins
};

// Light blue background with subtle pattern
$content .= "q\n";
$content .= "0.90 0.95 1 rg\n";
$content .= "0 0 $PAGE_W $PAGE_H re f\n";
// diagonal pattern lines
$content .= "0.85 0.92 1 RG 0.5 w\n";
for ($x = -200; $x <= 1040; $x += 22) {
    $content .= ($x) . " 0 m " . ($x + 600) . " $PAGE_H l S\n";
}
$content .= "Q\n";

// Main certificate white panel
$content .= "q\n1 1 1 rg 0.25 0.45 0.90 RG 2 w\n";
$content .= "60 70 722 455 re B\n";
$content .= "Q\n";

// Inner border
$content .= "q\n0.25 0.45 0.90 RG 1 w\n";
$content .= "78 88 686 419 re S\n";
$content .= "Q\n";

// Top curved ribbon feel (two bands)
$content .= "q\n0.15 0.35 0.75 rg\n";
$content .= "60 500 722 25 re f\n";
$content .= "0.95 0.80 0.35 rg\n";
$content .= "60 488 722 10 re f\n";
$content .= "Q\n";

// Logo area (top center)
$logoText = "Quiz Engine";
$logoX = $centerX($logoText, 16, 0.55);
$content .= "BT\n/F4 16 Tf 1 1 1 rg\n";
$content .= sprintf("%.2f 512 Td (%s) Tj\n", $logoX, $escapePdfText($logoText));
$content .= "ET\n";

// Title
$titleX = $centerX($title, 30, 0.58);
$content .= "BT\n/F4 30 Tf 0.15 0.25 0.65 rg\n";
$content .= sprintf("%.2f 440 Td (%s) Tj\n", $titleX, $escapePdfText($title));
$content .= "ET\n";

// Presented to
$presentedTo = "Presented to";
$presentedX = $centerX($presentedTo, 12, 0.52);
$content .= "BT\n/F1 12 Tf 0.35 0.35 0.35 rg\n";
$content .= sprintf("%.2f 395 Td (%s) Tj\n", $presentedX, $escapePdfText($presentedTo));
$content .= "ET\n";

// Recipient name in script-like italic
$nameX = $centerX($nameText, 32, 0.50);
$content .= "BT\n/F2 32 Tf 0.12 0.24 0.55 rg\n";
$content .= sprintf("%.2f 360 Td (%s) Tj\n", $nameX, $escapePdfText($nameText));
$content .= "ET\n";

// Intro line
$introX = $centerX($line1, 12, 0.50);
$content .= "BT\n/F1 12 Tf 0.35 0.35 0.35 rg\n";
$content .= sprintf("%.2f 415 Td (%s) Tj\n", $introX, $escapePdfText($line1));
$content .= "ET\n";

// Quiz name
$line2X = $centerX($line2, 14, 0.50);
$content .= "BT\n/F1 14 Tf 0.20 0.20 0.20 rg\n";
$content .= sprintf("%.2f 315 Td (%s) Tj\n", $line2X, $escapePdfText($line2));
$content .= "ET\n";

$subtitleX = $centerX($subtitle, 13, 0.52);
$content .= "BT\n/F1 13 Tf 0.20 0.20 0.20 rg\n";
$content .= sprintf("%.2f 292 Td (%s) Tj\n", $subtitleX, $escapePdfText($subtitle));
$content .= "ET\n";

// Badge/Score box (like template)
$boxX = 165; $boxY = 195; $boxW = 512; $boxH = 92;
$content .= "q\n0.98 0.95 0.82 rg 0.90 0.85 0.65 RG 1 w\n";
$content .= "$boxX $boxY $boxW $boxH re B\n";
$content .= "Q\n";

// Medal / badge icon (vector)
$mx = 260; $my = 240;
// ribbons
$content .= "q\n0.15 0.35 0.75 rg\n";
$content .= ($mx - 35) . " " . ($my + 30) . " " . ($mx - 10) . " " . ($my + 30) . " " . ($mx - 22) . " " . ($my - 5) . " c f\n";
$content .= ($mx + 10) . " " . ($my + 30) . " " . ($mx + 35) . " " . ($my + 30) . " " . ($mx + 22) . " " . ($my - 5) . " c f\n";
$content .= "Q\n";
// medal circle
$mcx = $mx; $mcy = $my + 18; $mr = 18;
$k = 0.5522847498;
$ox = $mr * $k;
$oy = $mr * $k;
$content .= "q\n0.95 0.80 0.35 rg 0.80 0.65 0.20 RG 1.5 w\n";
$content .= ($mcx) . " " . ($mcy + $mr) . " m\n";
$content .= ($mcx + $ox) . " " . ($mcy + $mr) . " " . ($mcx + $mr) . " " . ($mcy + $oy) . " " . ($mcx + $mr) . " " . ($mcy) . " c\n";
$content .= ($mcx + $mr) . " " . ($mcy - $oy) . " " . ($mcx + $ox) . " " . ($mcy - $mr) . " " . ($mcx) . " " . ($mcy - $mr) . " c\n";
$content .= ($mcx - $ox) . " " . ($mcy - $mr) . " " . ($mcx - $mr) . " " . ($mcy - $oy) . " " . ($mcx - $mr) . " " . ($mcy) . " c\n";
$content .= ($mcx - $mr) . " " . ($mcy + $oy) . " " . ($mcx - $ox) . " " . ($mcy + $mr) . " " . ($mcx) . " " . ($mcy + $mr) . " c\n";
$content .= "b\nQ\n";

// Badge title and score (centered inside box)
$badgeTextX = 310;
$content .= "BT\n/F4 18 Tf 0.15 0.15 0.15 rg\n";
$content .= $badgeTextX . " 247 Td (" . $escapePdfText($badgeTitle) . ") Tj\nET\n";
$content .= "BT\n/F1 13 Tf 0.20 0.20 0.20 rg\n";
$content .= $badgeTextX . " 227 Td (" . $escapePdfText($scoreLine) . ") Tj\nET\n";

// Completed date
$dateX = $centerX($lineDate, 11, 0.50);
$content .= "BT\n/F1 11 Tf 0.45 0.45 0.45 rg\n";
$content .= sprintf("%.2f 170 Td (%s) Tj\n", $dateX, $escapePdfText($lineDate));
$content .= "ET\n";

// Signature lines
$content .= "q\n0.65 0.75 0.90 RG 1 w\n";
$content .= "120 130 m 300 130 l S\n";
$content .= "542 130 m 722 130 l S\n";
$content .= "Q\n";

$content .= "BT\n/F1 9 Tf 0.35 0.35 0.35 rg\n";
$content .= "165 118 Td (Authorized Signature) Tj\nET\n";
$content .= "BT\n/F1 9 Tf 0.35 0.35 0.35 rg\n";
$content .= "605 118 Td (Candidate) Tj\nET\n";

$contentLength = strlen($content);

// Start building PDF
$objects = [];

// 1: Catalog
$objects[] = "<< /Type /Catalog /Pages 2 0 R >>\n";

// 2: Pages
$objects[] = "<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n";

// 3: Page
$objects[] = "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 842 595] /Resources << /Font << /F1 5 0 R /F2 6 0 R /F3 7 0 R /F4 8 0 R >> >> /Contents 4 0 R >>\n";

// 4: Content stream
$objects[] = "<< /Length $contentLength >>\nstream\n" . $content . "endstream\n";

// 5: Font (Helvetica)
$objects[] = "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\n";
// 6: Font (Times Bold Italic - elegant heading/name)
$objects[] = "<< /Type /Font /Subtype /Type1 /BaseFont /Times-BoldItalic >>\n";
// 7: Font (Times Italic - subtitle)
$objects[] = "<< /Type /Font /Subtype /Type1 /BaseFont /Times-Italic >>\n";
// 8: Font (Helvetica-Bold)
$objects[] = "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>\n";

// Build PDF output with xref
$pdf = "%PDF-1.4\n";
$offsets = [];

foreach ($objects as $index => $obj) {
    $id = $index + 1;
    $offsets[$id] = strlen($pdf);
    $pdf .= $id . " 0 obj\n" . $obj . "endobj\n";
}

$xrefOffset = strlen($pdf);
$count = count($objects) + 1;

$pdf .= "xref\n0 $count\n";
$pdf .= "0000000000 65535 f \n";

for ($i = 1; $i <= count($objects); $i++) {
    $pdf .= sprintf("%010d 00000 n \n", $offsets[$i]);
}

$pdf .= "trailer << /Size $count /Root 1 0 R >>\n";
$pdf .= "startxref\n$xrefOffset\n%%EOF";

$filename = 'Quiz_Certificate_' . preg_replace('/[^a-zA-Z0-9]+/', '_', $nameText) . '.pdf';

header('Content-Type: application/pdf');
header('Content-Disposition: attachment; filename="' . $filename . '"');
header('Content-Length: ' . strlen($pdf));

echo $pdf;
exit;
