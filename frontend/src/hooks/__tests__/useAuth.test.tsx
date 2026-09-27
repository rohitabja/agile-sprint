import { renderHook, waitFor } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { api } from '../../api/client'
import { createQueryWrapper, user } from './testUtils'
import { useAuth } from '../useAuth'

describe('useAuth', () => {
  afterEach(() => vi.restoreAllMocks())

  it('returns the authenticated user', async () => {
    vi.spyOn(api, 'auth').mockResolvedValue({ authenticated: true, user })
    const hook = renderHook(() => useAuth(), {
      wrapper: createQueryWrapper().wrapper,
    })

    await waitFor(() => expect(hook.result.current.user).toEqual(user))
    expect(hook.result.current.loading).toBe(false)
  })

  it('returns null when the user is unauthenticated', async () => {
    vi.spyOn(api, 'auth').mockResolvedValue({ authenticated: false, user })
    const hook = renderHook(() => useAuth(), {
      wrapper: createQueryWrapper().wrapper,
    })

    await waitFor(() => expect(hook.result.current.loading).toBe(false))
    expect(hook.result.current.user).toBeNull()
  })
})
