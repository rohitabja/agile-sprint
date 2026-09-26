import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { api } from '../api/client'
import { queryKeys } from '../api/queryKeys'
import type { Comment } from '../types'

export function useComments(taskId: string, enabled: boolean) {
  return useQuery({
    queryKey: queryKeys.comments(taskId),
    queryFn: () => api.comments(taskId),
    enabled: enabled && Boolean(taskId),
  })
}

export function useAddComment(taskId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (body: string) => api.addComment(taskId, body),
    onSuccess: (comment) => {
      queryClient.setQueryData<Comment[]>(
        queryKeys.comments(taskId),
        (current) => [...(current ?? []), comment],
      )
    },
  })
}
