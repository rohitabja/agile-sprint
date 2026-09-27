import { act, renderHook, waitFor } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { api } from '../../api/client'
import { queryKeys } from '../../api/queryKeys'
import { createQueryWrapper, workspace } from './testUtils'
import { useCreateWorkspace, useWorkspaces } from '../useWorkspaces'

describe('useWorkspaces', () => {
  afterEach(() => vi.restoreAllMocks())

  it('loads the current user workspaces', async () => {
    vi.spyOn(api, 'workspaces').mockResolvedValue([workspace])
    const { wrapper } = createQueryWrapper()
    const hook = renderHook(() => useWorkspaces(), { wrapper })

    await waitFor(() => expect(hook.result.current.data).toEqual([workspace]))
    expect(api.workspaces).toHaveBeenCalledOnce()
  })

  it('creates a workspace and invalidates the workspace query', async () => {
    vi.spyOn(api, 'createWorkspace').mockResolvedValue(workspace)
    const { client, wrapper } = createQueryWrapper()
    const invalidate = vi.spyOn(client, 'invalidateQueries').mockResolvedValue(undefined)
    const hook = renderHook(() => useCreateWorkspace(), { wrapper })
    const form = { name: workspace.name, description: workspace.description }

    await act(async () => {
      await hook.result.current.mutateAsync(form)
    })

    expect(api.createWorkspace).toHaveBeenCalledWith(
      form,
      expect.objectContaining({ client }),
    )
    expect(invalidate).toHaveBeenCalledWith({ queryKey: queryKeys.workspaces })
  })
})
