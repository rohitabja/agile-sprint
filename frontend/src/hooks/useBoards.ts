import { useQuery } from '@tanstack/react-query'
import { api } from '../api/client'
import { queryKeys } from '../api/queryKeys'

export function useBoards(workspaceId: string) {
  return useQuery({
    queryKey: queryKeys.boards(workspaceId),
    queryFn: () => api.boards(workspaceId),
    enabled: Boolean(workspaceId),
  })
}
