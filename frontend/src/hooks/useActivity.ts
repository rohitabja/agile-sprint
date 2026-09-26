import { useQuery } from '@tanstack/react-query'
import { api } from '../api/client'
import { queryKeys } from '../api/queryKeys'

export function useActivity(workspaceId: string) {
  return useQuery({
    queryKey: queryKeys.activity(workspaceId),
    queryFn: () => api.activity(workspaceId),
    enabled: Boolean(workspaceId),
    refetchInterval: 5000,
  })
}
