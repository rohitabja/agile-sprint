export type User = { id: string; name: string; username: string; email: string }
export type Workspace = {
  id: string
  name: string
  description: string
  ownerUsername: string
  memberCount: number
}
export type Member = {
  id: string
  userId: string
  username: string
  email: string
  role: 'ADMIN' | 'MEMBER'
}
export type Column = { id: string; name: string; key: string; position: number }
export type Board = {
  id: string
  workspaceId: string
  name: string
  columns: Column[]
}
export type Task = {
  id: string
  boardId: string
  workspaceId: string
  columnId: string
  title: string
  description: string
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT'
  assigneeId?: string
  assigneeUsername?: string
  createdByUsername: string
  position: number
  createdAt: string
  updatedAt: string
}
export type Activity = {
  id: string
  taskId?: string
  actorName: string
  type: string
  message: string
  createdAt: string
}
export type Comment = {
  id: string
  taskId: string
  authorName: string
  body: string
  createdAt: string
}
