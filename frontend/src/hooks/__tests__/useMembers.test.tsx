import { act, renderHook, waitFor } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { api } from '../../api/client'
import { queryKeys } from '../../api/queryKeys'
import { createQueryWrapper, member, workspace } from './testUtils'
import { useInviteMember, useMembers } from '../useMembers'

describe('useMembers', () => {
  afterEach(() => vi.restoreAllMocks())

  it('loads members for a workspace', async () => {
    vi.spyOn(api, 'members').mockResolvedValue([member])
    const hook = renderHook(() => useMembers(workspace.id), {
      wrapper: createQueryWrapper().wrapper,
    })

    await waitFor(() => expect(hook.result.current.data).toEqual([member]))
    expect(api.members).toHaveBeenCalledWith(workspace.id)
  })

  it('does not request members without a workspace identifier', async () => {
    const members = vi.spyOn(api, 'members')
    renderHook(() => useMembers(''), { wrapper: createQueryWrapper().wrapper })

    await act(async () => Promise.resolve())
    expect(members).not.toHaveBeenCalled()
  })

  it('invites a member and invalidates the member query', async () => {
    vi.spyOn(api, 'invite').mockResolvedValue(member)
    const { client, wrapper } = createQueryWrapper()
    const invalidate = vi.spyOn(client, 'invalidateQueries').mockResolvedValue(undefined)
    const hook = renderHook(() => useInviteMember(workspace.id), { wrapper })
    const invite = { username: member.username, email: member.email }

    await act(async () => {
      await hook.result.current.mutateAsync(invite)
    })

    expect(api.invite).toHaveBeenCalledWith(workspace.id, invite)
    expect(invalidate).toHaveBeenCalledWith({
      queryKey: queryKeys.members(workspace.id),
    })
  })
})
