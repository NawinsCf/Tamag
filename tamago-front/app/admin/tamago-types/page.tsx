import { redirect } from 'next/navigation';

export default function Page() {
  // Redirect legacy/dashed route to the canonical tamagotypes page
  redirect('/admin/tamagotypes');
}
