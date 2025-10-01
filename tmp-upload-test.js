const fs = require('fs');
const fetch = require('node-fetch');
const FormData = require('form-data');
(async () => {
  try {
    const filePath = 'test-upload.png';
    fs.writeFileSync(filePath, Buffer.from([137,80,78,71,13,10,26,10]));
    const f = new FormData();
    f.append('file', fs.createReadStream(filePath));
    const res = await fetch('http://localhost:3005/api/upload-image', { method: 'POST', body: f, headers: f.getHeaders() });
    console.log('status', res.status);
    const text = await res.text();
    console.log('body', text);
  } catch (e) {
    console.error(e);
  }
})();
