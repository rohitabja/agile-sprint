import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { api } from '../api/client'
import { queryKeys } from '../api/queryKeys'
import type { Task } from '../types'

export function useTasks(boardId: string) {
  return useQuery({
    queryKey: queryKeys.tasks(boardId),
    queryFn: () => api.tasks(boardId),
    enabled: Boolean(boardId),
  })
}

export function useCreateTask(workspaceId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({
      boardId,
      columnId,
      form,
    }: {
      boardId: string
      columnId?: string
      form: { title: string; description: string; priority: Task['priority'] }
    }) => api.createTask(boardId, { ...form, columnId }),
    onSuccess: (_task, { boardId }) =>
      Promise.all([
        queryClient.invalidateQueries({ queryKey: queryKeys.tasks(boardId) }),
        queryClient.invalidateQueries({
          queryKey: queryKeys.activity(workspaceId),
        }),
      ]),
  })
}

export function useMoveTask(workspaceId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ task, columnId }: { task: Task; columnId: string }) =>
      api.moveTask(task.id, columnId),
    onSuccess: (updated) =>
      Promise.all([
        queryClient.invalidateQueries({
          queryKey: queryKeys.tasks(updated.boardId),
        }),
        queryClient.invalidateQueries({
          queryKey: queryKeys.activity(workspaceId),
        }),
      ]),
  })
}
