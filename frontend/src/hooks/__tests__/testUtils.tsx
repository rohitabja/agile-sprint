import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import type { PropsWithChildren } from 'react'
import type {
  Activity,
  Board,
  Comment,
  Member,
  Task,
  User,
  Workspace,
} from '../../types'

export const workspace: Workspace = {
  id: 'workspace-1',
  name: 'Sprint',
  description: '',
  ownerUsername: 'alice',
  memberCount: 1,
}

export const board: Board = {
  id: 'board-1',
  workspaceId: workspace.id,
  name: 'Main',
  columns: [{ id: 'todo', name: 'To Do', key: 'todo', position: 0 }],
}

export const task: Task = {
  id: 'task-1',
  boardId: board.id,
  workspaceId: workspace.id,
  columnId: 'todo',
  title: 'Task',
  description: '',
  priority: 'MEDIUM',
  createdByUsername: 'alice',
  position: 0,
  createdAt: '',
  updatedAt: '',
}

export const member: Member = {
  id: 'member-1',
  userId: 'user-1',
  username: 'alice',
  email: 'alice@example.com',
  role: 'ADMIN',
}

export const activity: Activity = {
  id: 'activity-1',
  taskId: task.id,
  actorName: 'Alice',
  type: 'TASK_CREATED',
  message: 'created a task',
  createdAt: '',
}

export const comment: Comment = {
  id: 'comment-1',
  taskId: task.id,
  authorName: 'Alice',
  body: 'Looks good',
  createdAt: '',
}

export const user: User = {
  id: 'user-1',
  name: 'Alice',
  username: 'alice',
  email: 'alice@example.com',
}

export function createQueryWrapper() {
  const client = new QueryClient({
    defaultOptions: { queries: { retry: false, gcTime: Infinity } },
  })

  return {
    client,
    wrapper: ({ children }: PropsWithChildren) => (
      <QueryClientProvider client={client}>{children}</QueryClientProvider>
    ),
  }
}
