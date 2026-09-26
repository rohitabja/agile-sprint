export const queryKeys = {
  auth: ['auth'] as const,
  workspaces: ['workspaces'] as const,
  boards: (workspaceId: string) =>
    ['workspaces', workspaceId, 'boards'] as const,
  members: (workspaceId: string) =>
    ['workspaces', workspaceId, 'members'] as const,
  activity: (workspaceId: string) =>
    ['workspaces', workspaceId, 'activity'] as const,
  tasks: (boardId: string) => ['boards', boardId, 'tasks'] as const,
  comments: (taskId: string) => ['tasks', taskId, 'comments'] as const,
}
