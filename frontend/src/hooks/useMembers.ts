import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { api } from '../api/client'
import { queryKeys } from '../api/queryKeys'

export function useMembers(workspaceId: string) {
  return useQuery({
    queryKey: queryKeys.members(workspaceId),
    queryFn: () => api.members(workspaceId),
    enabled: Boolean(workspaceId),
  })
}

export function useInviteMember(workspaceId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (invite: { username: string; email: string }) =>
      api.invite(workspaceId, invite),
    onSuccess: () =>
      queryClient.invalidateQueries({
        queryKey: queryKeys.members(workspaceId),
      }),
  })
}
