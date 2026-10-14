
import { useEffect, useState } from "react";
import ReactMarkdown from "react-markdown";
import "./App.css";
import {
  getTasks,
  createTask,
  updateTask,
  deleteTask,
  suggestTaskSteps,
  getSubtasks,
  createSubtask,
  updateSubtask,
  deleteSubtask,
} from "./api/taskApi";

function App() {
  const [tasks, setTasks] = useState([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [priority, setPriority] = useState("MEDIUM");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [editingTaskId, setEditingTaskId] = useState(null);
  const [filter, setFilter] = useState("ALL");
  const [aiTaskDescription, setAiTaskDescription] = useState("");
const [aiSuggestion, setAiSuggestion] = useState("");
const [aiLoading, setAiLoading] = useState(false);
const [aiError, setAiError] = useState("");
const [subtasksByTask, setSubtasksByTask] = useState({});
const [subtaskTitles, setSubtaskTitles] = useState({});
const [subtaskErrors, setSubtaskErrors] = useState({});
const [selectedTaskId, setSelectedTaskId] = useState("");
const [savingSubtasks, setSavingSubtasks] = useState(false);
const [saveSubtasksMessage, setSaveSubtasksMessage] = useState("");
const [savedSuggestionKey, setSavedSuggestionKey] = useState("");

  const totalTasks = tasks.length;

const completedTasks = tasks.filter(
  (task) => task.completed
).length;

const pendingTasks = totalTasks - completedTasks;

const filteredTasks = tasks.filter((task) => {
  if (filter === "PENDING") return !task.completed;
  if (filter === "COMPLETED") return task.completed;
  return true;
});
  async function loadTasks() {
    try {
      setError("");
      const data = await getTasks();
      setTasks(Array.isArray(data) ? data : [data]);
    } catch (err) {
      setError(err.message || "Unable to load tasks.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadTasks();
  }, []);

  
async function handleSubmit(event) {
  event.preventDefault();

  if (!title.trim()) {
    setError("Please enter a task title.");
    return;
  }

  try {
    setError("");

    const taskData = {
      title: title.trim(),
      description: description.trim(),
      priority,
      completed: editingTaskId !== null
        ? tasks.find((task) => task.id === editingTaskId)?.completed ?? false
        : false,
    };

    if (editingTaskId !== null) {
      await updateTask(editingTaskId, taskData);
    } else {
      await createTask(taskData);
    }

    setTitle("");
    setDescription("");
    setPriority("MEDIUM");
    setEditingTaskId(null);

    await loadTasks();
  } catch (err) {
    setError(err.message || "Unable to save task.");
  }
}

  
async function handleToggleTask(task) {
  try {
    setError("");

    await updateTask(task.id, {
      title: task.title,
      description: task.description || "",
      priority: task.priority,
      completed: !task.completed,
    });

    await loadTasks();
  } catch (err) {
    setError(err.message || "Failed to update task.");
  }
}

async function handleDeleteTask(id) {
  try {
    setError("");

    await deleteTask(id);
    await loadTasks();
  } catch (err) {
    setError(err.message || "Failed to delete task.");
  }
}
function handleEditTask(task) {
  setEditingTaskId(task.id);
  setTitle(task.title);
  setDescription(task.description || "");
  setPriority(task.priority || "MEDIUM");

  window.scrollTo({ top: 0, behavior: "smooth" });
}
async function handleGenerateAiSteps(event) {
  event.preventDefault();

  const task = aiTaskDescription.trim();

  if (!task) {
    setAiError("Please enter a task description.");
    setAiSuggestion("");
    return;
  }

  setAiLoading(true);
  setAiError("");
  setAiSuggestion("");

  try {
    const result = await suggestTaskSteps(task);
    setAiSuggestion(result.suggestion);
  } catch (err) {
    console.error("AI task suggestion failed:", err);
    setAiError(
      "Could not generate suggestions. Check that Spring Boot and Ollama are running."
    );
  } finally {
    setAiLoading(false);
  }
}

async function handleBreakIntoSteps(task) {
  const taskDescription =
    `${task.title}${task.description ? `: ${task.description}` : ""}`.trim();

  if (!taskDescription) {
    setAiError("This task has no title to plan.");
    return;
  }

  setAiTaskDescription(task.title);
  setAiLoading(true);
  setAiError("");
  setAiSuggestion("");

  try {
    const result = await suggestTaskSteps(taskDescription);
    setAiSuggestion(result.suggestion);
  } catch (error) {
    console.error("AI task breakdown failed:", error);
    setAiError(
      "Could not generate task steps. Check that Spring Boot and Ollama are running."
    );
  } finally {
    setAiLoading(false);
  }
}

async function loadSubtasks(taskId) {
  try {
    const data = await getSubtasks(taskId);
    setSubtasksByTask((previous) => ({
      ...previous,
      [taskId]: data,
    }));
    setSubtaskErrors((previous) => ({
      ...previous,
      [taskId]: "",
    }));
  } catch (error) {
    setSubtaskErrors((previous) => ({
      ...previous,
      [taskId]: error.message || "Failed to load subtasks.",
    }));
  }
}

async function handleAddSubtask(taskId) {
  const title = (subtaskTitles[taskId] || "").trim();

  if (!title) {
    setSubtaskErrors((previous) => ({
      ...previous,
      [taskId]: "Enter a subtask title.",
    }));
    return;
  }

  try {
    await createSubtask(taskId, title);

    setSubtaskTitles((previous) => ({
      ...previous,
      [taskId]: "",
    }));

    await loadSubtasks(taskId);
  } catch (error) {
    setSubtaskErrors((previous) => ({
      ...previous,
      [taskId]: error.message || "Failed to create subtask.",
    }));
  }
}

async function handleToggleSubtask(taskId, subtask) {
  try {
    await updateSubtask(subtask.id, !subtask.completed);
    await loadSubtasks(taskId);
  } catch (error) {
    setSubtaskErrors((previous) => ({
      ...previous,
      [taskId]: error.message || "Failed to update subtask.",
    }));
  }
}

async function handleDeleteSubtask(taskId, subtaskId) {
  try {
    await deleteSubtask(subtaskId);
    await loadSubtasks(taskId);
  } catch (error) {
    setSubtaskErrors((previous) => ({
      ...previous,
      [taskId]: error.message || "Failed to delete subtask.",
    }));
  }
}


async function handleSaveAiSubtasks() {
  if (!selectedTaskId || !aiSuggestion.trim()) {
    setSaveSubtasksMessage(
      "Generate suggestions and select a task first."
    );
    return;
  }

  // Extract numbered steps from common AI Markdown formats.
  const titles = aiSuggestion
    .split(/\r?\n/)
    .map((line) => line.trim())
    .map((line) =>
      line
        .replace(/^#{1,6}\s*/, "")
        .replace(/^\*\*(.*?)\*\*$/, "$1")
        .replace(/^[-*]\s+/, "")
        .trim()
    )
    .filter((line) =>
      /^(?:step\s*)?\d+\s*[:.)-]\s+\S/i.test(line)
    )
    .map((line) =>
      line
        .replace(/^(?:step\s*)?\d+\s*[:.)-]\s*/i, "")
        .replace(/\*\*/g, "")
        .trim()
    )
    .filter((title) => title.length > 0 && title.length <= 200);

  const uniqueTitles = [...new Set(titles)];

  if (uniqueTitles.length === 0) {
    setSaveSubtasksMessage(
      "No numbered steps were detected. Please try generating the suggestions again."
    );
    return;
  }

  setSavingSubtasks(true);
  setSaveSubtasksMessage("");

  let savedCount = 0;

  try {
    for (const title of uniqueTitles) {
      await createSubtask(Number(selectedTaskId), title);
      savedCount++;
    }

    await loadSubtasks(Number(selectedTaskId));
    setSavedSuggestionKey(`${selectedTaskId}:${aiSuggestion}`);

    setSaveSubtasksMessage(
      `Saved ${savedCount} AI-generated subtasks successfully.`
    );
  } catch (error) {
    console.error("Saving AI subtasks failed:", error);

    setSaveSubtasksMessage(
      `Saved ${savedCount} of ${uniqueTitles.length} subtasks. Check the task's subtask list before retrying.`
    );
  } finally {
    setSavingSubtasks(false);
  }
}



  return (
    <main className="app">
      <header className="header">
        <h1>SmartTaskAI</h1>
        <p>Your personal task management dashboard</p>
      </header>

      <section className="task-card">
        <h2>Create a task</h2>

        <form onSubmit={handleSubmit}>
          <label htmlFor="task-title">Task title</label>
          <input
            id="task-title"
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            placeholder="What do you need to do?"
          />

          <label htmlFor="task-description">Description</label>
          <input
            id="task-description"
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            placeholder="Add details (optional)"
          />

          <label htmlFor="task-priority">Priority</label>
          <select
            id="task-priority"
            value={priority}
            onChange={(event) => setPriority(event.target.value)}
          >
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
          </select>

          <button type="submit">
  {editingTaskId !== null ? "Save changes" : "Add task"}
</button>

{editingTaskId !== null && (
  <button
    type="button"
    onClick={() => {
      setEditingTaskId(null);
      setTitle("");
      setDescription("");
      setPriority("MEDIUM");
      setError("");
    }}
  >
    Cancel editing
  </button>
)}
        </form>

        {error && <p role="alert" className="error">{error}</p>}
      
<section className="ai-planner">
  <h2>AI Task Planner</h2>
  <p>Turn a task into simple, actionable steps.</p>

  <form onSubmit={handleGenerateAiSteps}>
    <textarea
      value={aiTaskDescription}
      onChange={(event) =>
        setAiTaskDescription(event.target.value)
      }
      placeholder="Example: Learn Java collections"
      rows={3}
      maxLength={500}
      required
    />

    <button type="submit" disabled={aiLoading}>
      {aiLoading ? "Generating..." : "Generate AI Steps"}
    </button>
  </form>

  {aiError && (
    <p className="ai-error" role="alert">
      {aiError}
    </p>
  )}

  


{aiSuggestion && (
  <div className="ai-result" aria-live="polite">
    <h3>Suggested Steps</h3>

    <div className="ai-suggestion">
      <ReactMarkdown>{aiSuggestion}</ReactMarkdown>
    </div>

    <label htmlFor="save-subtasks-task">
      Save steps to task
    </label>

    <select
      id="save-subtasks-task"
      value={selectedTaskId}
      onChange={(event) => {
        setSelectedTaskId(event.target.value);
        setSaveSubtasksMessage("");
      }}
    >
      <option value="">Select a task</option>
      {tasks.map((task) => (
        <option key={task.id} value={task.id}>
          {task.title}
        </option>
      ))}
    </select>

    <button
  type="button"
  onClick={handleSaveAiSubtasks}
  disabled={
    savingSubtasks ||
    !selectedTaskId ||
    savedSuggestionKey === `${selectedTaskId}:${aiSuggestion}`
  }
>
  {savingSubtasks
    ? "Saving..."
    : savedSuggestionKey === `${selectedTaskId}:${aiSuggestion}`
      ? "Steps already saved"
      : "Save as subtasks"}
</button>

    {saveSubtasksMessage && (
      <p role="status">{saveSubtasksMessage}</p>
    )}
  </div>
)}


</section>

      </section>
      

      <section className="task-list">
        <section className="task-stats">
  <p>Total: {totalTasks}</p>
  <p>Pending: {pendingTasks}</p>
  <p>Completed: {completedTasks}</p>
</section>
        <h2>Your tasks</h2>
        <div className="task-filters">
  <button type="button" onClick={() => setFilter("ALL")}>
    All
  </button>

  <button type="button" onClick={() => setFilter("PENDING")}>
    Pending
  </button>

  <button type="button" onClick={() => setFilter("COMPLETED")}>
    Completed
  </button>
  
  
</div>
        {loading && <p>Loading tasks...</p>}
        {!loading && tasks.length === 0 && <p>No tasks yet. Add one above!</p>}

        {filteredTasks.map((task) => (
          
<article className="task-item" key={task.id}>
  <h3>{task.title}</h3>
  <p>{task.description || "No description"}</p>
  <span>Priority: {task.priority}</span>
  <span>{task.completed ? "Completed" : "Pending"}</span>

  <div className="task-actions">
    <button
      type="button"
      onClick={() => handleToggleTask(task)}
    >
      {task.completed ? "Mark pending" : "Mark complete"}
    </button>
    <button
  type="button"
  onClick={() => handleEditTask(task)}
>
  Edit
</button>

<button
  type="button"
  onClick={() => handleBreakIntoSteps(task)}
  disabled={aiLoading}
>
  {aiLoading ? "Generating..." : "Break into steps"}
</button>


    <button
      type="button"
      className="delete-button"
      onClick={() => handleDeleteTask(task.id)}
    >
      Delete
    </button>
    
  </div>
  
<div className="subtasks-section">
  <h4>Subtasks</h4>
  
{(() => {
  const subtasks = subtasksByTask[task.id] || [];
  const completedCount = subtasks.filter(
    (subtask) => subtask.completed
  ).length;
  const progress = subtasks.length
    ? Math.round((completedCount / subtasks.length) * 100)
    : 0;

  return (
    <div className="subtask-progress">
      <div className="progress-label">
        <span>
          {completedCount} of {subtasks.length} completed
        </span>
        <span>{progress}%</span>
      </div>

      <div
        className="progress-track"
        role="progressbar"
        aria-label="Subtask completion progress"
        aria-valuemin={0}
        aria-valuemax={100}
        aria-valuenow={progress}
      >
        <div
          className="progress-fill"
          style={{ width: `${progress}%` }}
        />
      </div>
    </div>
  );
})()}


  <button
    type="button"
    onClick={() => loadSubtasks(task.id)}
  >
    Load subtasks
  </button>

  {(subtasksByTask[task.id] || []).map((subtask) => (
    <div className="subtask-item" key={subtask.id}>
      <label>
        <input
          type="checkbox"
          checked={subtask.completed}
          onChange={() => handleToggleSubtask(task.id, subtask)}
        />
        <span className={subtask.completed ? "subtask-completed" : ""}>
          {subtask.title}
        </span>
      </label>

      <button
        type="button"
        onClick={() => handleDeleteSubtask(task.id, subtask.id)}
      >
        Delete
      </button>
    </div>
  ))}

  <form
    onSubmit={(event) => {
      event.preventDefault();
      handleAddSubtask(task.id);
    }}
  >
    <input
      value={subtaskTitles[task.id] || ""}
      onChange={(event) =>
        setSubtaskTitles((previous) => ({
          ...previous,
          [task.id]: event.target.value,
        }))
      }
      placeholder="Add a subtask"
      maxLength={200}
    />

    <button type="submit">Add subtask</button>
  </form>

  {subtaskErrors[task.id] && (
    <p className="ai-error" role="alert">
      {subtaskErrors[task.id]}
    </p>
  )}
</div>

</article>

        ))}
      </section>

      <footer>Learn. Build. Achieve.</footer>
    </main>
  );
}

export default App;
