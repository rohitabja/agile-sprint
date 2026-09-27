import { act, renderHook, waitFor } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { api } from '../../api/client'
import { queryKeys } from '../../api/queryKeys'
import { board, createQueryWrapper, task, workspace } from './testUtils'
import { useCreateTask, useMoveTask, useTasks } from '../useTasks'

describe('useTasks', () => {
  afterEach(() => vi.restoreAllMocks())

  it('loads tasks for a board', async () => {
    vi.spyOn(api, 'tasks').mockResolvedValue([task])
    const hook = renderHook(() => useTasks(board.id), {
      wrapper: createQueryWrapper().wrapper,
    })

    await waitFor(() => expect(hook.result.current.data).toEqual([task]))
    expect(api.tasks).toHaveBeenCalledWith(board.id)
  })

  it('does not request tasks without a board identifier', async () => {
    const fetchTasks = vi.spyOn(api, 'tasks')
    renderHook(() => useTasks(''), { wrapper: createQueryWrapper().wrapper })

    await act(async () => Promise.resolve())
    expect(fetchTasks).not.toHaveBeenCalled()
  })

  it('creates tasks and invalidates board tasks and workspace activity', async () => {
    vi.spyOn(api, 'createTask').mockResolvedValue(task)
    const { client, wrapper } = createQueryWrapper()
    const invalidate = vi.spyOn(client, 'invalidateQueries').mockResolvedValue(undefined)
    const hook = renderHook(() => useCreateTask(workspace.id), { wrapper })
    const form = { title: 'New task', description: '', priority: 'HIGH' as const }

    await act(async () => {
      await hook.result.current.mutateAsync({ boardId: board.id, columnId: 'todo', form })
    })

    expect(api.createTask).toHaveBeenCalledWith(board.id, { ...form, columnId: 'todo' })
    expect(invalidate).toHaveBeenCalledWith({ queryKey: queryKeys.tasks(board.id) })
    expect(invalidate).toHaveBeenCalledWith({
      queryKey: queryKeys.activity(workspace.id),
    })
  })

  it('moves tasks and invalidates board tasks and workspace activity', async () => {
    vi.spyOn(api, 'moveTask').mockResolvedValue(task)
    const { client, wrapper } = createQueryWrapper()
    const invalidate = vi.spyOn(client, 'invalidateQueries').mockResolvedValue(undefined)
    const hook = renderHook(() => useMoveTask(workspace.id), { wrapper })

    await act(async () => {
      await hook.result.current.mutateAsync({ task, columnId: 'done' })
    })

    expect(api.moveTask).toHaveBeenCalledWith(task.id, 'done')
    expect(invalidate).toHaveBeenCalledWith({ queryKey: queryKeys.tasks(board.id) })
    expect(invalidate).toHaveBeenCalledWith({
      queryKey: queryKeys.activity(workspace.id),
    })
  })
})
