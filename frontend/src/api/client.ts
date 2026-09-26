import type {
  Activity,
  Board,
  Comment,
  Member,
  Task,
  User,
  Workspace,
} from '../types'

export class ApiError extends Error {
  constructor(
    public status: number,
    message: string,
  ) {
    super(message)
  }
}
async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(path, {
    credentials: 'include',
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  })
  const body = await response.json().catch(() => ({}))
  if (!response.ok)
    throw new ApiError(
      response.status,
      body.error || `Request failed (${response.status})`,
    )
  return body as T
}
export const api = {
  auth: () =>
    request<{ authenticated: boolean; user: User }>('/api/auth/status'),
  workspaces: () => request<Workspace[]>('/api/workspaces'),
  createWorkspace: (body: { name: string; description: string }) =>
    request<Workspace>('/api/workspaces', {
      method: 'POST',
      body: JSON.stringify(body),
    }),
  members: (id: string) => request<Member[]>(`/api/workspaces/${id}/members`),
  invite: (id: string, body: { username: string; email: string }) =>
    request<Member>(`/api/workspaces/${id}/invites`, {
      method: 'POST',
      body: JSON.stringify(body),
    }),
  boards: (id: string) => request<Board[]>(`/api/workspaces/${id}/boards`),
  tasks: (id: string) => request<Task[]>(`/api/boards/${id}/tasks`),
  createTask: (id: string, body: Partial<Task>) =>
    request<Task>(`/api/boards/${id}/tasks`, {
      method: 'POST',
      body: JSON.stringify(body),
    }),
  moveTask: (id: string, columnId: string) =>
    request<Task>(`/api/tasks/${id}/move`, {
      method: 'POST',
      body: JSON.stringify({ columnId }),
    }),
  activity: (id: string) =>
    request<Activity[]>(`/api/workspaces/${id}/activity`),
  comments: (id: string) => request<Comment[]>(`/api/tasks/${id}/comments`),
  addComment: (id: string, body: string) =>
    request<Comment>(`/api/tasks/${id}/comments`, {
      method: 'POST',
      body: JSON.stringify({ body }),
    }),
}
