import { useQuery } from '@tanstack/react-query'
import { api } from '../api/client'
import { queryKeys } from '../api/queryKeys'
import type { User } from '../types'

export function useAuth() {
  const query = useQuery({
    queryKey: queryKeys.auth,
    queryFn: async (): Promise<User | null> => {
      const status = await api.auth()
      return status.authenticated ? status.user : null
    },
    retry: false,
  })

  return { user: query.data ?? null, loading: query.isPending }
}
