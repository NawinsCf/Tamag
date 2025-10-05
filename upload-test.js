const fs = require('fs');
const FormData = require('form-data');
(async () => {
  try {
    const f = new FormData();
    f.append('file', fs.createReadStream('test-upload.png'));
    const res = await fetch('http://localhost:3000/api/upload-image', { method: 'POST', body: f, headers: f.getHeaders() });
    console.log('status', res.status);
    const text = await res.text();
    try { console.log(JSON.parse(text)); } catch(e) { console.log(text); }
  } catch (e) { console.error('err', e); }
})();
