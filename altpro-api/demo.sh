#!/bin/bash
set -e
BASE=http://localhost:8080

echo "1) Create project"
proj=$(curl -s -X POST $BASE/api/projects -H "Content-Type: application/json" -d '{"name":"Demo Project","description":"For exam","members":["user1"]}')
projId=$(echo "$proj" | jq -r '.id // ._id')
echo "Project created: $projId"

echo "2) Create task"
task=$(curl -s -X POST $BASE/api/tasks -H "Content-Type: application/json" -d '{"projectId":"'"$projId"'","title":"Demo Task","description":"Do it","status":"TODO","priority":1,"assignee":"user1"}')
taskId=$(echo "$task" | jq -r '.id // ._id')
echo "Task created: $taskId"

echo "3) Create comment"
comment=$(curl -s -X POST $BASE/api/comments -H "Content-Type: application/json" -d '{"taskId":"'"$taskId"'","author":"user2","text":"Please review"}')
commentId=$(echo "$comment" | jq -r '.id // ._id')
echo "Comment created: $commentId"

echo "4) List tasks for project"
curl -s $BASE/api/tasks/project/$projId | jq .

echo "5) List comments for task"
curl -s $BASE/api/comments/task/$taskId | jq .

echo "6) Show 404 example"
curl -s -o /dev/null -w "%{http_code}\n" $BASE/api/projects/doesnotexist

echo "Demo finished"
