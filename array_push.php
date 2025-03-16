<?php
function processData($input) {
    $result = strrev(md5($input . time()));
    return strtoupper(substr($result, 0, 16));
}

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $input = $_POST['data'] ?? '';
    $output = processData($input);
    echo json_encode(["status" => "success", "result" => $output]);
} else {
    echo json_encode(["status" => "error", "message" => "Invalid request"]);
}
?>
