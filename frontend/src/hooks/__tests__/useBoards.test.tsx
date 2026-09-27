import { act, renderHook, waitFor } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { api } from '../../api/client'
import { board, createQueryWrapper, workspace } from './testUtils'
import { useBoards } from '../useBoards'

describe('useBoards', () => {
  afterEach(() => vi.restoreAllMocks())

  it('loads boards for a workspace', async () => {
    vi.spyOn(api, 'boards').mockResolvedValue([board])
    const hook = renderHook(() => useBoards(workspace.id), {
      wrapper: createQueryWrapper().wrapper,
    })

    await waitFor(() => expect(hook.result.current.data).toEqual([board]))
    expect(api.boards).toHaveBeenCalledWith(workspace.id)
  })

  it('does not request boards without a workspace identifier', async () => {
    const boards = vi.spyOn(api, 'boards')
    renderHook(() => useBoards(''), { wrapper: createQueryWrapper().wrapper })

    await act(async () => Promise.resolve())
    expect(boards).not.toHaveBeenCalled()
  })
})
