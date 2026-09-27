import { act, renderHook, waitFor } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { api } from '../../api/client'
import { activity, createQueryWrapper, workspace } from './testUtils'
import { useActivity } from '../useActivity'

describe('useActivity', () => {
  afterEach(() => vi.restoreAllMocks())

  it('loads workspace activity', async () => {
    vi.spyOn(api, 'activity').mockResolvedValue([activity])
    const hook = renderHook(() => useActivity(workspace.id), {
      wrapper: createQueryWrapper().wrapper,
    })

    await waitFor(() => expect(hook.result.current.data).toEqual([activity]))
    expect(api.activity).toHaveBeenCalledWith(workspace.id)
  })

  it('does not request activity without a workspace identifier', async () => {
    const fetchActivity = vi.spyOn(api, 'activity')
    renderHook(() => useActivity(''), { wrapper: createQueryWrapper().wrapper })

    await act(async () => Promise.resolve())
    expect(fetchActivity).not.toHaveBeenCalled()
  })
})
