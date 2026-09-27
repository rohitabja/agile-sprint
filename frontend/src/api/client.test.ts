import { afterEach, describe, expect, it, vi } from 'vitest'
import { ApiError, api } from './client'

function mockFetch(body: unknown, ok = true, status = 200) {
  const fetchMock = vi.fn().mockResolvedValue({
    ok,
    status,
    json: vi.fn().mockResolvedValue(body),
  })
  vi.stubGlobal('fetch', fetchMock)
  return fetchMock
}

describe('api client', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it.each([
    ['auth', () => api.auth(), '/api/auth/status'],
    ['workspaces', () => api.workspaces(), '/api/workspaces'],
    ['members', () => api.members('workspace-1'), '/api/workspaces/workspace-1/members'],
    ['boards', () => api.boards('workspace-1'), '/api/workspaces/workspace-1/boards'],
    ['tasks', () => api.tasks('board-1'), '/api/boards/board-1/tasks'],
    ['activity', () => api.activity('workspace-1'), '/api/workspaces/workspace-1/activity'],
    ['comments', () => api.comments('task-1'), '/api/tasks/task-1/comments'],
  ])('requests %s using the expected endpoint', async (_name, request, endpoint) => {
    const fetchMock = mockFetch([])

    await request()

    expect(fetchMock).toHaveBeenCalledWith(endpoint, {
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
    })
  })

  it.each([
    [
      'create workspace',
      () => api.createWorkspace({ name: 'Sprint', description: 'Planning' }),
      '/api/workspaces',
      { name: 'Sprint', description: 'Planning' },
    ],
    [
      'invite member',
      () => api.invite('workspace-1', { username: 'alice', email: 'a@example.com' }),
      '/api/workspaces/workspace-1/invites',
      { username: 'alice', email: 'a@example.com' },
    ],
    [
      'create task',
      () => api.createTask('board-1', { title: 'Test' }),
      '/api/boards/board-1/tasks',
      { title: 'Test' },
    ],
  ])('sends the expected JSON when it attempts to %s', async (_name, request, endpoint, body) => {
    const fetchMock = mockFetch({})

    await request()

    expect(fetchMock).toHaveBeenCalledWith(endpoint, {
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      method: 'POST',
      body: JSON.stringify(body),
    })
  })

  it('moves tasks and adds comments with their request bodies', async () => {
    const fetchMock = mockFetch({})

    await api.moveTask('task-1', 'column-2')
    await api.addComment('task-1', 'Looks good')

    expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/tasks/task-1/move', expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({ columnId: 'column-2' }),
    }))
    expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/tasks/task-1/comments', expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({ body: 'Looks good' }),
    }))
  })

  it('throws ApiError with the server error message', async () => {
    mockFetch({ error: 'Workspace is unavailable' }, false, 403)

    await expect(api.workspaces()).rejects.toEqual(
      expect.objectContaining({
        constructor: ApiError,
        status: 403,
        message: 'Workspace is unavailable',
      }),
    )
  })

  it('uses a status-based message when the server has no error message', async () => {
    mockFetch({}, false, 503)

    await expect(api.workspaces()).rejects.toMatchObject({
      status: 503,
      message: 'Request failed (503)',
    })
  })

  it('tolerates a non-JSON error response', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: false,
        status: 502,
        json: vi.fn().mockRejectedValue(new Error('Invalid JSON')),
      }),
    )

    await expect(api.workspaces()).rejects.toMatchObject({
      status: 502,
      message: 'Request failed (502)',
    })
  })
})
