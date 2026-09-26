import { useEffect, useMemo, useState } from 'react'
import { ApiError } from '../api/client'
import { useActivity } from './useActivity'
import { useBoards } from './useBoards'
import { useCreateTask, useMoveTask, useTasks } from './useTasks'
import { useInviteMember, useMembers } from './useMembers'
import type { Board, Task } from '../types'

export function useWorkspaceBoard(
  workspaceId: string,
  notify: (message: string) => void,
) {
  const [selectedBoardId, setSelectedBoardId] = useState<string | null>(null)
  const boardsQuery = useBoards(workspaceId)
  const membersQuery = useMembers(workspaceId)
  const activityQuery = useActivity(workspaceId)
  const boards = boardsQuery.data ?? []
  const board =
    boards.find((item) => item.id === selectedBoardId) ?? boards[0] ?? null
  const boardId = board?.id ?? ''
  const tasksQuery = useTasks(boardId)
  const createTaskMutation = useCreateTask(workspaceId)
  const moveTaskMutation = useMoveTask(workspaceId)
  const inviteMutation = useInviteMember(workspaceId)
  useEffect(() => {
    if (boardsQuery.error) {
      notify(
        boardsQuery.error instanceof ApiError
          ? boardsQuery.error.message
          : 'Could not load boards',
      )
    }
  }, [boardsQuery.error, notify])
  useEffect(() => {
    if (membersQuery.error) {
      notify(
        membersQuery.error instanceof ApiError
          ? membersQuery.error.message
          : 'Could not load workspace members',
      )
    }
  }, [membersQuery.error, notify])
  useEffect(() => {
    if (activityQuery.error) {
      notify(
        activityQuery.error instanceof ApiError
          ? activityQuery.error.message
          : 'Could not refresh activity',
      )
    }
  }, [activityQuery.error, notify])
  useEffect(() => {
    if (tasksQuery.error) {
      notify(
        tasksQuery.error instanceof ApiError
          ? tasksQuery.error.message
          : 'Could not load tasks',
      )
    }
  }, [tasksQuery.error, notify])
  const members = membersQuery.data ?? []
  const activity = activityQuery.data ?? []
  const tasks = tasksQuery.data ?? []
  const columns = board?.columns ?? []
  const grouped = useMemo(
    () =>
      columns.map((column) => ({
        column,
        tasks: tasks.filter((task) => task.columnId === column.id),
      })),
    [columns, tasks],
  )

  const selectBoard = (selected: Board) => setSelectedBoardId(selected.id)

  const createTask = async (form: {
    title: string
    description: string
    priority: Task['priority']
  }) => {
    if (board) {
      await createTaskMutation.mutateAsync({
        boardId: board.id,
        columnId: columns[0]?.id,
        form,
      })
    }
  }

  const moveTask = async (task: Task, columnId: string) => {
    await moveTaskMutation.mutateAsync({ task, columnId })
  }

  const inviteMember = async (invite: { username: string; email: string }) => {
    await inviteMutation.mutateAsync(invite)
  }

  return {
    boards,
    board,
    tasks,
    members,
    activity,
    columns,
    grouped,
    selectBoard,
    createTask,
    moveTask,
    inviteMember,
    loading: boardsQuery.isPending,
  }
}
