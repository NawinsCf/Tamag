import fs from 'fs';
import path from 'path';

export default function handler(req, res) {
  if (req.method !== 'POST') return res.status(405).json({ error: 'method not allowed' });
  const { filename } = req.body || {};
  if (!filename) return res.status(400).json({ error: 'filename required' });
  try {
    const safe = path.basename(String(filename));
    const filePath = path.join(process.cwd(), 'public', 'tamagoimage', safe);
    if (fs.existsSync(filePath)) {
      fs.unlinkSync(filePath);
      return res.json({ ok: true });
    } else {
      return res.status(404).json({ error: 'not found' });
    }
  } catch (e) {
    console.error('delete-image error', e);
    return res.status(500).json({ error: 'delete error', detail: String(e) });
  }
}
