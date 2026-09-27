import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { act, renderHook, waitFor } from '@testing-library/react'
import type { PropsWithChildren } from 'react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { api } from '../../api/client'
import { queryKeys } from '../../api/queryKeys'
import type { Activity, Board, Comment, Member, Task, User, Workspace } from '../../types'
import { useActivity } from '../useActivity'
import { useAuth } from '../useAuth'
import { useBoards } from '../useBoards'
import { useAddComment, useComments } from '../useComments'
import { useInviteMember, useMembers } from '../useMembers'
import { useCreateTask, useMoveTask, useTasks } from '../useTasks'
import { useCreateWorkspace, useWorkspaces } from '../useWorkspaces'
import { useWorkspaceBoard } from '../useWorkspaceBoard'

const workspace: Workspace = {
  id: 'workspace-1',
  name: 'Sprint',
  description: '',
  ownerUsername: 'alice',
  memberCount: 1,
}
const board: Board = {
  id: 'board-1',
  workspaceId: workspace.id,
  name: 'Main',
  columns: [{ id: 'todo', name: 'To Do', key: 'todo', position: 0 }],
}
const task: Task = {
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
const member: Member = {
  id: 'member-1',
  userId: 'user-1',
  username: 'alice',
  email: 'alice@example.com',
  role: 'ADMIN',
}
const activity: Activity = {
  id: 'activity-1',
  taskId: task.id,
  actorName: 'Alice',
  type: 'TASK_CREATED',
  message: 'created a task',
  createdAt: '',
}
const comment: Comment = {
  id: 'comment-1',
  taskId: task.id,
  authorName: 'Alice',
  body: 'Looks good',
  createdAt: '',
}
const user: User = {
  id: 'user-1',
  name: 'Alice',
  username: 'alice',
  email: 'alice@example.com',
}

const clients = new Set<QueryClient>()
function createWrapper() {
  const client = new QueryClient({
    defaultOptions: { queries: { retry: false, gcTime: Infinity } },
  })
  clients.add(client)
  return {
    client,
    wrapper: ({ children }: PropsWithChildren) => (
      <QueryClientProvider client={client}>{children}</QueryClientProvider>
    ),
  }
}

describe('React Query hooks', () => {
  afterEach(() => {
    clients.forEach((client) => client.clear())
    clients.clear()
    vi.restoreAllMocks()
  })

  it('loads workspaces, boards, members, activity, tasks, and comments', async () => {
    vi.spyOn(api, 'workspaces').mockResolvedValue([workspace])
    vi.spyOn(api, 'boards').mockResolvedValue([board])
    vi.spyOn(api, 'members').mockResolvedValue([member])
    vi.spyOn(api, 'activity').mockResolvedValue([activity])
    vi.spyOn(api, 'tasks').mockResolvedValue([task])
    vi.spyOn(api, 'comments').mockResolvedValue([comment])
    const queries = createWrapper()

    const workspaces = renderHook(() => useWorkspaces(), { wrapper: queries.wrapper })
    const boards = renderHook(() => useBoards(workspace.id), { wrapper: queries.wrapper })
    const members = renderHook(() => useMembers(workspace.id), { wrapper: queries.wrapper })
    const activities = renderHook(() => useActivity(workspace.id), { wrapper: queries.wrapper })
    const tasks = renderHook(() => useTasks(board.id), { wrapper: queries.wrapper })
    const comments = renderHook(() => useComments(task.id, true), { wrapper: queries.wrapper })

    await waitFor(() => {
      expect(workspaces.result.current.data).toEqual([workspace])
      expect(boards.result.current.data).toEqual([board])
      expect(members.result.current.data).toEqual([member])
      expect(activities.result.current.data).toEqual([activity])
      expect(tasks.result.current.data).toEqual([task])
      expect(comments.result.current.data).toEqual([comment])
    })
    expect(api.activity).toHaveBeenCalledWith(workspace.id)
  })

  it('does not run parameterized queries when identifiers are missing', async () => {
    const boards = vi.spyOn(api, 'boards')
    const members = vi.spyOn(api, 'members')
    const activities = vi.spyOn(api, 'activity')
    const tasks = vi.spyOn(api, 'tasks')
    const comments = vi.spyOn(api, 'comments')
    const wrapper = createWrapper().wrapper

    renderHook(() => useBoards(''), { wrapper })
    renderHook(() => useMembers(''), { wrapper })
    renderHook(() => useActivity(''), { wrapper })
    renderHook(() => useTasks(''), { wrapper })
    renderHook(() => useComments('', true), { wrapper })

    await act(async () => Promise.resolve())
    expect(boards).not.toHaveBeenCalled()
    expect(members).not.toHaveBeenCalled()
    expect(activities).not.toHaveBeenCalled()
    expect(tasks).not.toHaveBeenCalled()
    expect(comments).not.toHaveBeenCalled()
  })

  it('returns an authenticated user and maps unauthenticated status to null', async () => {
    const auth = vi.spyOn(api, 'auth')
    const wrapper = createWrapper().wrapper
    auth.mockResolvedValueOnce({ authenticated: true, user })
    const authenticated = renderHook(() => useAuth(), { wrapper })
    await waitFor(() => expect(authenticated.result.current.user).toEqual(user))

    auth.mockResolvedValueOnce({ authenticated: false, user })
    const unauthenticated = renderHook(() => useAuth(), {
      wrapper: createWrapper().wrapper,
    })
    await waitFor(() => expect(unauthenticated.result.current.loading).toBe(false))
    expect(unauthenticated.result.current.user).toBeNull()
  })

  it('invalidates the affected queries after workspace, task, move, and invite mutations', async () => {
    vi.spyOn(api, 'createWorkspace').mockResolvedValue(workspace)
    vi.spyOn(api, 'createTask').mockResolvedValue(task)
    vi.spyOn(api, 'moveTask').mockResolvedValue(task)
    vi.spyOn(api, 'invite').mockResolvedValue(member)
    const { client, wrapper } = createWrapper()
    const invalidate = vi.spyOn(client, 'invalidateQueries').mockResolvedValue(undefined)
    const workspaceMutation = renderHook(() => useCreateWorkspace(), { wrapper })
    const createTask = renderHook(() => useCreateTask(workspace.id), { wrapper })
    const moveTask = renderHook(() => useMoveTask(workspace.id), { wrapper })
    const invite = renderHook(() => useInviteMember(workspace.id), { wrapper })

    await act(async () => {
      await workspaceMutation.result.current.mutateAsync({
        name: workspace.name,
        description: workspace.description,
      })
      await createTask.result.current.mutateAsync({
        boardId: board.id,
        columnId: 'todo',
        form: { title: task.title, description: '', priority: task.priority },
      })
      await moveTask.result.current.mutateAsync({ task, columnId: 'done' })
      await invite.result.current.mutateAsync({ username: member.username, email: member.email })
    })

    expect(api.createTask).toHaveBeenCalledWith(board.id, {
      title: task.title,
      description: '',
      priority: task.priority,
      columnId: 'todo',
    })
    expect(api.moveTask).toHaveBeenCalledWith(task.id, 'done')
    expect(api.invite).toHaveBeenCalledWith(workspace.id, {
      username: member.username,
      email: member.email,
    })
    expect(invalidate).toHaveBeenCalledWith({ queryKey: queryKeys.workspaces })
    expect(invalidate).toHaveBeenCalledWith({ queryKey: queryKeys.tasks(board.id) })
    expect(invalidate).toHaveBeenCalledWith({ queryKey: queryKeys.activity(workspace.id) })
    expect(invalidate).toHaveBeenCalledWith({ queryKey: queryKeys.members(workspace.id) })
  })

  it('appends a successfully created comment to the cached comments', async () => {
    vi.spyOn(api, 'addComment').mockResolvedValue(comment)
    const { client, wrapper } = createWrapper()
    client.setQueryData(queryKeys.comments(task.id), [])
    const mutation = renderHook(() => useAddComment(task.id), { wrapper })

    await act(async () => {
      await mutation.result.current.mutateAsync(comment.body)
    })

    expect(api.addComment).toHaveBeenCalledWith(task.id, comment.body)
    expect(client.getQueryData(queryKeys.comments(task.id))).toEqual([comment])
  })

  it('groups workspace board tasks by column and forwards board actions', async () => {
    vi.spyOn(api, 'boards').mockResolvedValue([board])
    vi.spyOn(api, 'members').mockResolvedValue([member])
    vi.spyOn(api, 'activity').mockResolvedValue([activity])
    vi.spyOn(api, 'tasks').mockResolvedValue([task])
    vi.spyOn(api, 'createTask').mockResolvedValue(task)
    vi.spyOn(api, 'moveTask').mockResolvedValue(task)
    vi.spyOn(api, 'invite').mockResolvedValue(member)
    const { wrapper } = createWrapper()
    const notify = vi.fn()
    const hook = renderHook(() => useWorkspaceBoard(workspace.id, notify), { wrapper })

    await waitFor(() => expect(hook.result.current.loading).toBe(false))
    expect(hook.result.current.board).toEqual(board)
    expect(hook.result.current.grouped).toEqual([{ column: board.columns[0], tasks: [task] }])

    await act(async () => {
      await hook.result.current.createTask({
        title: 'New task',
        description: '',
        priority: 'HIGH',
      })
      await hook.result.current.moveTask(task, 'done')
      await hook.result.current.inviteMember({ username: 'bob', email: 'bob@example.com' })
    })
    expect(api.createTask).toHaveBeenCalledWith(board.id, {
      title: 'New task',
      description: '',
      priority: 'HIGH',
      columnId: 'todo',
    })
    expect(api.moveTask).toHaveBeenCalledWith(task.id, 'done')
    expect(api.invite).toHaveBeenCalledWith(workspace.id, {
      username: 'bob',
      email: 'bob@example.com',
    })
    expect(notify).not.toHaveBeenCalled()
  })
})
