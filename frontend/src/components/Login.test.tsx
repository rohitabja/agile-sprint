import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { Login } from './Login'

describe('Login', () => {
  it('shows the Keycloak sign-in link', () => {
    render(<Login />)

    expect(
      screen.getByRole('link', { name: 'Sign in with Keycloak' }),
    ).toHaveAttribute('href', '/oauth2/authorization/keycloak')
    expect(
      screen.getByRole('heading', { name: 'Ship work together.' }),
    ).toBeInTheDocument()
  })
})
