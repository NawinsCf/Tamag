import LoginForm from "../components/LoginForm";

export default function Home() {
  return (
    <div className="min-h-screen flex items-center justify-center p-6">
      <div className="w-full max-w-xl">
        <LoginForm showRegisterLink={true} />
      </div>
    </div>
  );
}
