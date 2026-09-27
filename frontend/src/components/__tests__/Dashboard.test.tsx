import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen, waitFor, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { useLocation, MemoryRouter } from 'react-router-dom'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { ToastContext } from '../../contexts/ToastContext'
import type { User, Workspace } from '../../types'
import { Dashboard } from '../Dashboard'

const user: User = {
  id: 'user-1',
  name: 'Alice Smith',
  username: 'alice',
  email: 'alice@example.com',
}

function LocationDisplay() {
  const location = useLocation()
  return <output aria-label="Current route">{location.pathname}</output>
}

function renderDashboard() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  })
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <ToastContext.Provider value={vi.fn()}>
          <Dashboard user={user} />
          <LocationDisplay />
        </ToastContext.Provider>
      </MemoryRouter>
    </QueryClientProvider>,
  )
}

describe('Dashboard', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('shows the empty state when the user has no workspaces', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: true,
        json: async () => [],
      }),
    )

    renderDashboard()

    expect(
      await screen.findByText('Your first sprint starts here'),
    ).toBeInTheDocument()
    expect(
      screen.getByRole('heading', { name: 'Good to see you, Alice.' }),
    ).toBeInTheDocument()
  })

  it('creates a workspace and navigates to it', async () => {
    const workspace: Workspace = {
      id: 'workspace-1',
      name: 'Sprint planning',
      description: 'Plan the next release',
      ownerUsername: 'alice',
      memberCount: 1,
    }
    const fetchMock = vi
      .fn()
      .mockResolvedValueOnce({
        ok: true,
        json: async () => [],
      })
      .mockResolvedValueOnce({
        ok: true,
        json: async () => workspace,
      })
    vi.stubGlobal('fetch', fetchMock)

    const user = userEvent.setup()
    renderDashboard()

    await user.click(
      await screen.findByRole('button', { name: 'New workspace' }),
    )
    const dialog = screen.getByRole('dialog', { name: 'Create workspace' })
    await user.type(
      within(dialog).getByRole('textbox', { name: 'Workspace name' }),
      workspace.name,
    )
    await user.type(
      within(dialog).getByRole('textbox', { name: 'Description (optional)' }),
      workspace.description,
    )
    await user.click(within(dialog).getByRole('button', { name: 'Create' }))

    await waitFor(() => {
      expect(screen.getByLabelText('Current route')).toHaveTextContent(
        '/workspaces/workspace-1',
      )
    })
    expect(fetchMock).toHaveBeenCalledWith(
      '/api/workspaces',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({
          name: workspace.name,
          description: workspace.description,
        }),
      }),
    )
  })
})
