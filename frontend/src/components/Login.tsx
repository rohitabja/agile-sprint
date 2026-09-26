import { Box, Button, Paper, Typography } from '@mui/material'

export function Login() {
  return (
    <Box className="login-page">
      <Paper className="login-card" elevation={5}>
        <Typography variant="overline" color="primary">
          AgileSprint
        </Typography>
        <Typography variant="h2" gutterBottom>
          Ship work together.
        </Typography>
        <Typography color="text.secondary" paragraph>
          Plan sprints, keep context close to every task, and see progress at a
          glance.
        </Typography>
        <Button
          variant="contained"
          size="large"
          fullWidth
          href="/oauth2/authorization/keycloak"
        >
          Sign in with Keycloak
        </Button>
      </Paper>
    </Box>
  )
}
