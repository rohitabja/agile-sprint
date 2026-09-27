import {
  render,
  screen,
  waitForElementToBeRemoved,
  within,
} from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { ToastContext } from '../../contexts/ToastContext'
import { useWorkspaceBoard } from '../../hooks/useWorkspaceBoard'
import type { Activity, Board, Member, Task, User } from '../../types'
import { WorkspaceBoard } from '../WorkspaceBoard'

vi.mock('../../hooks/useWorkspaceBoard', () => ({
  useWorkspaceBoard: vi.fn(),
}))
vi.mock('../TaskCard', () => ({
  TaskCard: ({ task }: { task: Task }) => <div>{task.title}</div>,
}))

const user: User = {
  id: 'user-1',
  name: 'Alice Smith',
  username: 'alice',
  email: 'alice@example.com',
}
const board: Board = {
  id: 'board-1',
  workspaceId: 'workspace-1',
  name: 'Main board',
  columns: [{ id: 'todo', name: 'To Do', key: 'todo', position: 0 }],
}
const member: Member = {
  id: 'member-1',
  userId: user.id,
  username: user.username,
  email: user.email,
  role: 'ADMIN',
}
const activity: Activity = {
  id: 'activity-1',
  actorName: 'Alice',
  type: 'TASK_CREATED',
  message: 'created a task',
  createdAt: '',
}
const task: Task = {
  id: 'task-1',
  boardId: board.id,
  workspaceId: board.workspaceId,
  columnId: 'todo',
  title: 'Plan sprint',
  description: '',
  priority: 'MEDIUM',
  createdByUsername: 'alice',
  position: 0,
  createdAt: '',
  updatedAt: '',
}

describe('WorkspaceBoard', () => {
  const toast = vi.fn()
  const state: ReturnType<typeof useWorkspaceBoard> = {
    boards: [board],
    board,
    tasks: [task],
    members: [member],
    activity: [activity],
    columns: board.columns,
    grouped: [{ column: board.columns[0], tasks: [task] }],
    selectBoard: vi.fn(),
    createTask: vi.fn().mockResolvedValue(undefined),
    moveTask: vi.fn().mockResolvedValue(undefined),
    inviteMember: vi.fn().mockResolvedValue(undefined),
    loading: false,
  }

  function renderBoard() {
    return render(
      <ToastContext.Provider value={toast}>
        <MemoryRouter initialEntries={['/workspaces/workspace-1']}>
          <Routes>
            <Route
              path="/workspaces/:workspaceId"
              element={<WorkspaceBoard user={user} />}
            />
          </Routes>
        </MemoryRouter>
      </ToastContext.Provider>,
    )
  }

  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(useWorkspaceBoard).mockReturnValue(state)
  })

  it('shows a loading indicator while board data is loading', () => {
    vi.mocked(useWorkspaceBoard).mockReturnValue({ ...state, loading: true })
    renderBoard()
    expect(screen.getByRole('progressbar')).toBeInTheDocument()
  })

  it('renders board content and submits task and invite forms', async () => {
    const userInteraction = userEvent.setup()
    renderBoard()

    expect(screen.getByRole('heading', { name: 'Main board' })).toBeInTheDocument()
    expect(screen.getByText('Plan sprint')).toBeInTheDocument()
    expect(screen.getByText(/created a task/)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Invite' })).toBeEnabled()

    await userInteraction.click(screen.getByRole('button', { name: 'New task' }))
    const taskDialog = screen.getByRole('dialog', { name: 'New task' })
    await userInteraction.type(within(taskDialog).getByRole('textbox', { name: 'Title' }), 'Add tests')
    await userInteraction.type(
      within(taskDialog).getByRole('textbox', { name: 'Description' }),
      'Cover the board',
    )
    await userInteraction.click(within(taskDialog).getByRole('button', { name: 'Create task' }))
    expect(state.createTask).toHaveBeenCalledWith({
      title: 'Add tests',
      description: 'Cover the board',
      priority: 'MEDIUM',
    })
    expect(toast).toHaveBeenCalledWith('Task created')

    await waitForElementToBeRemoved(taskDialog)
    await userInteraction.click(screen.getByRole('button', { name: 'Invite' }))
    const inviteDialog = screen.getByRole('dialog', { name: 'Invite a teammate' })
    await userInteraction.type(
      within(inviteDialog).getByRole('textbox', { name: 'Keycloak username' }),
      'bob',
    )
    await userInteraction.type(
      within(inviteDialog).getByRole('textbox', { name: 'Email (optional)' }),
      'bob@example.com',
    )
    await userInteraction.click(within(inviteDialog).getByRole('button', { name: 'Invite' }))
    expect(state.inviteMember).toHaveBeenCalledWith({
      username: 'bob',
      email: 'bob@example.com',
    })
    expect(toast).toHaveBeenCalledWith('Invitation added')
  })

  it('disables invitations for non-admin users', () => {
    vi.mocked(useWorkspaceBoard).mockReturnValue({
      ...state,
      members: [{ ...member, role: 'MEMBER' }],
    })

    renderBoard()

    expect(screen.getByRole('button', { name: 'Invite' })).toBeDisabled()
  })
})
