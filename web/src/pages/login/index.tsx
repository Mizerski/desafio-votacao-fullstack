import { LoginForm } from './component/login-form'

export function LoginPage() {
  return (
    <div className="flex min-h-svh flex-col items-center justify-center gap-6 p-6 md:p-10 bg-background ">
      <div className="w-full max-w-sm">
        <LoginForm />
      </div>
    </div>
  )
}
