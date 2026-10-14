
const API_URL = "http://localhost:8080/api/tasks";

export async function getTasks() {
  const response = await fetch(API_URL);

  if (!response.ok) {
    throw new Error("Failed to fetch tasks");
  }

  return response.json();
}

export async function createTask(task) {
  const response = await fetch(API_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(task),
  });

  if (!response.ok) {
    throw new Error("Failed to create task");
  }

  return response.json();
}

export async function updateTask(id, task) {
  const response = await fetch(`${API_URL}/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(task),
  });

  if (!response.ok) {
    throw new Error("Failed to update task");
  }

  return response.json();
}

export async function deleteTask(id) {
  const response = await fetch(`${API_URL}/${id}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    throw new Error("Failed to delete task");
  }
}

export async function suggestTaskSteps(taskDescription) {
  const response = await fetch("http://localhost:8080/api/ai/suggest", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ taskDescription }),
  });

  if (!response.ok) {
    throw new Error("Failed to generate AI task steps");
  }

  return response.json();
}

export async function getSubtasks(taskId) {
  const response = await fetch(`${API_URL}/${taskId}/subtasks`);

  if (!response.ok) {
    throw new Error("Failed to fetch subtasks");
  }

  return response.json();
}

export async function createSubtask(taskId, title) {
  const response = await fetch(`${API_URL}/${taskId}/subtasks`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ title }),
  });

  if (!response.ok) {
    throw new Error("Failed to create subtask");
  }

  return response.json();
}

export async function updateSubtask(subtaskId, completed) {
  const response = await fetch(
    `http://localhost:8080/api/subtasks/${subtaskId}`,
    {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ completed }),
    }
  );

  if (!response.ok) {
    throw new Error("Failed to update subtask");
  }

  return response.json();
}

export async function deleteSubtask(subtaskId) {
  const response = await fetch(
    `http://localhost:8080/api/subtasks/${subtaskId}`,
    { method: "DELETE" }
  );

  if (!response.ok) {
    throw new Error("Failed to delete subtask");
  }
}



