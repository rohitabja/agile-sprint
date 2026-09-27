import { act, renderHook, waitFor } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { api } from '../../api/client'
import { board, createQueryWrapper, member, task, workspace } from './testUtils'
import { useWorkspaceBoard } from '../useWorkspaceBoard'

describe('useWorkspaceBoard', () => {
  afterEach(() => vi.restoreAllMocks())

  it('groups loaded tasks by column and forwards create, move, and invite actions', async () => {
    vi.spyOn(api, 'boards').mockResolvedValue([board])
    vi.spyOn(api, 'members').mockResolvedValue([member])
    vi.spyOn(api, 'activity').mockResolvedValue([])
    vi.spyOn(api, 'tasks').mockResolvedValue([task])
    vi.spyOn(api, 'createTask').mockResolvedValue(task)
    vi.spyOn(api, 'moveTask').mockResolvedValue(task)
    vi.spyOn(api, 'invite').mockResolvedValue(member)
    const { wrapper } = createQueryWrapper()
    const notify = vi.fn()
    const hook = renderHook(() => useWorkspaceBoard(workspace.id, notify), { wrapper })

    await waitFor(() => expect(hook.result.current.loading).toBe(false))
    expect(hook.result.current.board).toEqual(board)
    expect(hook.result.current.grouped).toEqual([
      { column: board.columns[0], tasks: [task] },
    ])

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
