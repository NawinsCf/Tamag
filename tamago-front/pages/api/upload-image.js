import fs from 'fs';
import path from 'path';
import { randomUUID } from 'crypto';

export const config = {
  api: {
    bodyParser: false,
  },
};

import formidable from 'formidable';

export default async function handler(req, res) {
  if (req.method !== 'POST') return res.status(405).end();
  const form = new formidable.IncomingForm();
  form.parse(req, (err, fields, files) => {
    if (err) {
      console.error('form parse error', err);
      return res.status(500).json({ error: 'parse error', detail: String(err) });
    }

    // Support different shapes: files.file may be array or single object
    let file = files && (files.file || files['file']);
    if (Array.isArray(file)) file = file[0];
    if (!file) {
      console.error('no file in upload, files:', files);
      return res.status(400).json({ error: 'no file' });
    }

    try {
      const uploadsDir = path.join(process.cwd(), 'public', 'tamagoimage');
      if (!fs.existsSync(uploadsDir)) fs.mkdirSync(uploadsDir, { recursive: true });

      // support both formidable v1 (file.path) and v2 (file.filepath)
      const filepath = file.filepath || file.path || (file?.[0] && (file[0].filepath || file[0].path));
      if (!filepath || !fs.existsSync(filepath)) {
        console.error('uploaded file not found on disk:', filepath);
        return res.status(500).json({ error: 'uploaded file not found', detail: String(filepath) });
      }

      const stats = fs.statSync(filepath);
      const maxBytes = 5 * 1024 * 1024; // 5MB
      if (stats.size > maxBytes) {
        console.error('file too large', stats.size);
        return res.status(400).json({ error: 'file too large', maxBytes });
      }

      // determine extension from original filename or filepath
      const original = file.originalFilename || file.originalname || path.basename(filepath) || 'upload';
      let ext = path.extname(original).toLowerCase();
      const allowed = ['.png', '.jpg', '.jpeg', '.gif', '.webp', '.bmp', '.svg'];
      if (!ext) {
        // try to guess from mime in fields (if any) or default to .png
        ext = '.png';
      }
      if (!allowed.includes(ext)) {
        console.error('invalid extension', ext);
        return res.status(400).json({ error: 'invalid file type', allowed });
      }

      const data = fs.readFileSync(filepath);

      // generate timestamp-uuid.ext filename
      const filename = `${Date.now()}-${randomUUID()}${ext}`;
      const dest = path.join(uploadsDir, filename);
      fs.writeFileSync(dest, data);
      return res.json({ filename });
    } catch (e) {
      console.error('save upload error', e);
      return res.status(500).json({ error: 'save error', detail: String(e) });
    }
  });
}
