import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { api } from '../api/client'
import { ToastContext } from '../contexts/ToastContext'
import type { Board, Comment, Task } from '../types'
import { TaskCard } from './TaskCard'

const task: Task = {
  id: 'task-1',
  boardId: 'board-1',
  workspaceId: 'workspace-1',
  columnId: 'todo',
  title: 'Prepare release',
  description: 'Write release notes',
  priority: 'HIGH',
  assigneeUsername: 'alice',
  createdByUsername: 'alice',
  position: 0,
  createdAt: '',
  updatedAt: '',
}
const columns: Board['columns'] = [
  { id: 'todo', name: 'To Do', key: 'todo', position: 0 },
  { id: 'doing', name: 'In Progress', key: 'doing', position: 1 },
]
const comment: Comment = {
  id: 'comment-1',
  taskId: task.id,
  authorName: 'Alice',
  body: 'Ready for review',
  createdAt: '',
}

describe('TaskCard', () => {
  const toast = vi.fn()
  afterEach(() => vi.restoreAllMocks())

  function renderCard(onMove: (task: Task, columnId: string) => Promise<void>) {
    const client = new QueryClient({
      defaultOptions: { queries: { retry: false, gcTime: Infinity } },
    })
    return render(
      <QueryClientProvider client={client}>
        <ToastContext.Provider value={toast}>
          <TaskCard task={task} columns={columns} onMove={onMove} />
        </ToastContext.Provider>
      </QueryClientProvider>,
    )
  }

  it('renders task details and calls onMove when another column is selected', async () => {
    const onMove = vi.fn().mockResolvedValue(undefined)
    const user = userEvent.setup()
    renderCard(onMove)

    expect(screen.getByText(task.title)).toBeInTheDocument()
    expect(screen.getByText(task.description)).toBeInTheDocument()
    expect(screen.getByText('HIGH')).toBeInTheDocument()
    expect(screen.getByText('a')).toBeInTheDocument()

    await user.click(screen.getByRole('combobox'))
    await user.click(await screen.findByRole('option', { name: 'In Progress' }))
    expect(onMove).toHaveBeenCalledWith(task, 'doing')
  })

  it('opens comments and adds a trimmed comment', async () => {
    vi.spyOn(api, 'comments').mockResolvedValue([comment])
    vi.spyOn(api, 'addComment').mockResolvedValue(comment)
    const user = userEvent.setup()
    renderCard(vi.fn().mockResolvedValue(undefined))

    await user.click(screen.getByRole('button', { name: 'Comments' }))
    expect(await screen.findByText('Ready for review')).toBeInTheDocument()
    expect(api.comments).toHaveBeenCalledWith(task.id)
    const input = screen.getByRole('textbox', { name: 'Add a comment' })
    await user.type(input, '  More detail  ')
    await user.click(screen.getByRole('button', { name: 'Comment' }))

    await waitFor(() => expect(api.addComment).toHaveBeenCalledWith(task.id, '  More detail  '))
    expect(input).toHaveValue('')
  })
})
