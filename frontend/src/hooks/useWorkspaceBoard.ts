import { useEffect, useMemo, useState } from 'react'
import { api, ApiError } from '../api/client'
import type { Activity, Board, Member, Task } from '../types'

export function useWorkspaceBoard(
  workspaceId: string,
  notify: (message: string) => void,
) {
  const [boards, setBoards] = useState<Board[]>([])
  const [board, setBoard] = useState<Board | null>(null)
  const [tasks, setTasks] = useState<Task[]>([])
  const [members, setMembers] = useState<Member[]>([])
  const [activity, setActivity] = useState<Activity[]>([])

  const load = async () => {
    try {
      const [loadedBoards, loadedMembers, loadedActivity] = await Promise.all([
        api.boards(workspaceId),
        api.members(workspaceId),
        api.activity(workspaceId),
      ])
      setBoards(loadedBoards)
      setMembers(loadedMembers)
      setActivity(loadedActivity)
      const selected =
        board && loadedBoards.some((item) => item.id === board.id)
          ? board
          : loadedBoards[0]
      if (selected) {
        setBoard(selected)
        setTasks(await api.tasks(selected.id))
      }
    } catch (error) {
      notify(
        error instanceof ApiError ? error.message : 'Could not load workspace',
      )
    }
  }

  useEffect(() => {
    void load()
  }, [workspaceId])

  useEffect(() => {
    const timer = window.setInterval(() => {
      void api
        .activity(workspaceId)
        .then(setActivity)
        .catch((error) =>
          notify(
            error instanceof ApiError
              ? error.message
              : 'Could not refresh activity',
          ),
        )
    }, 5000)
    return () => window.clearInterval(timer)
  }, [workspaceId, notify])

  const columns = board?.columns ?? []
  const grouped = useMemo(
    () =>
      columns.map((column) => ({
        column,
        tasks: tasks.filter((task) => task.columnId === column.id),
      })),
    [columns, tasks],
  )

  const selectBoard = async (selected: Board) => {
    setBoard(selected)
    setTasks(await api.tasks(selected.id))
  }

  const createTask = async (form: {
    title: string
    description: string
    priority: Task['priority']
  }) => {
    if (!board) return
    const task = await api.createTask(board.id, {
      ...form,
      columnId: columns[0]?.id,
    })
    setTasks((current) => [...current, task])
  }

  const moveTask = async (task: Task, columnId: string) => {
    const updated = await api.moveTask(task.id, columnId)
    setTasks((current) =>
      current.map((item) => (item.id === updated.id ? updated : item)),
    )
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
    setMembers,
  }
}
