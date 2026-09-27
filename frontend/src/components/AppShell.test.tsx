import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { describe, expect, it, vi } from 'vitest'
import type { User } from '../types'
import { AppShell } from './AppShell'

vi.mock('./Dashboard', () => ({
  Dashboard: () => <div>Dashboard route</div>,
}))
vi.mock('./WorkspaceBoard', () => ({
  WorkspaceBoard: () => <div>Workspace route</div>,
}))

const user: User = {
  id: 'user-1',
  name: 'Alice Smith',
  username: 'alice',
  email: 'alice@example.com',
}

describe('AppShell', () => {
  it('renders the dashboard at the root route and shows the user', () => {
    render(
      <MemoryRouter initialEntries={['/']}>
        <AppShell user={user} />
      </MemoryRouter>,
    )

    expect(screen.getByText('Dashboard route')).toBeInTheDocument()
    expect(screen.getByText('Alice Smith')).toBeInTheDocument()
    expect(
      screen.getByText(
        (_, element) => element?.tagName === 'H6' && element.textContent === 'AgileSprint',
      ),
    ).toBeInTheDocument()
  })

  it('renders the workspace route', () => {
    render(
      <MemoryRouter initialEntries={['/workspaces/workspace-1']}>
        <AppShell user={user} />
      </MemoryRouter>,
    )

    expect(screen.getByText('Workspace route')).toBeInTheDocument()
  })

  it('posts the logout form when the user signs out', async () => {
    const submit = vi
      .spyOn(HTMLFormElement.prototype, 'submit')
      .mockImplementation(() => undefined)
    render(
      <MemoryRouter>
        <AppShell user={user} />
      </MemoryRouter>,
    )

    screen.getByRole('button', { name: 'Sign out' }).click()

    expect(submit).toHaveBeenCalledOnce()
    expect(document.querySelector('form')).toHaveAttribute('method', 'POST')
    expect(document.querySelector('form')).toHaveAttribute('action', '/logout')
    document.querySelector('form')?.remove()
    submit.mockRestore()
  })
})
