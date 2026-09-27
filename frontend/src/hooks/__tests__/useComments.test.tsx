import { act, renderHook, waitFor } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { api } from '../../api/client'
import { queryKeys } from '../../api/queryKeys'
import { comment, createQueryWrapper, task } from './testUtils'
import { useAddComment, useComments } from '../useComments'

describe('useComments', () => {
  afterEach(() => vi.restoreAllMocks())

  it('loads comments when enabled', async () => {
    vi.spyOn(api, 'comments').mockResolvedValue([comment])
    const hook = renderHook(() => useComments(task.id, true), {
      wrapper: createQueryWrapper().wrapper,
    })

    await waitFor(() => expect(hook.result.current.data).toEqual([comment]))
    expect(api.comments).toHaveBeenCalledWith(task.id)
  })

  it('does not request comments when disabled or missing a task identifier', async () => {
    const fetchComments = vi.spyOn(api, 'comments')
    const wrapper = createQueryWrapper().wrapper

    renderHook(() => useComments(task.id, false), { wrapper })
    renderHook(() => useComments('', true), { wrapper })

    await act(async () => Promise.resolve())
    expect(fetchComments).not.toHaveBeenCalled()
  })

  it('appends a successfully added comment to the cached comments', async () => {
    vi.spyOn(api, 'addComment').mockResolvedValue(comment)
    const { client, wrapper } = createQueryWrapper()
    client.setQueryData(queryKeys.comments(task.id), [])
    const hook = renderHook(() => useAddComment(task.id), { wrapper })

    await act(async () => {
      await hook.result.current.mutateAsync(comment.body)
    })

    expect(api.addComment).toHaveBeenCalledWith(task.id, comment.body)
    expect(client.getQueryData(queryKeys.comments(task.id))).toEqual([comment])
  })
})
