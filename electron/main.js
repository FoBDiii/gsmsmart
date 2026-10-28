const { app, BrowserWindow, shell, Menu } = require('electron');
const path = require('path');

// Una sola ventana abierta a la vez
if (!app.requestSingleInstanceLock()) { app.quit(); }

let win;
function createWindow() {
  win = new BrowserWindow({
    width: 1280, height: 860, minWidth: 380, minHeight: 600,
    title: 'GSMsmart', icon: path.join(__dirname, '..', 'build', 'icon.png'),
    backgroundColor: '#F2F4F8', autoHideMenuBar: true,
    webPreferences: { contextIsolation: true, nodeIntegration: false, spellcheck: true }
  });
  Menu.setApplicationMenu(null);
  win.loadFile(path.join(__dirname, '..', 'www', 'index.html'));

  // Enlaces externos (WhatsApp, webs) en el navegador del sistema
  win.webContents.setWindowOpenHandler(({ url }) => {
    if (/^https?:|^mailto:|^tel:|^whatsapp:/.test(url)) shell.openExternal(url);
    return { action: 'deny' };
  });
  win.webContents.on('will-navigate', (e, url) => {
    if (!url.startsWith('file://')) { e.preventDefault(); shell.openExternal(url); }
  });
  // Ctrl+R recarga, F11 pantalla completa, Ctrl +/- zoom
  win.webContents.on('before-input-event', (e, input) => {
    if (input.type !== 'keyDown') return;
    const k = input.key.toLowerCase();
    if (input.control && k === 'r') { win.reload(); e.preventDefault(); }
    if (k === 'f11') { win.setFullScreen(!win.isFullScreen()); e.preventDefault(); }
    if (input.control && (k === '+' || k === '=')) { win.webContents.setZoomLevel(win.webContents.getZoomLevel() + 0.5); e.preventDefault(); }
    if (input.control && k === '-') { win.webContents.setZoomLevel(win.webContents.getZoomLevel() - 0.5); e.preventDefault(); }
    if (input.control && k === '0') { win.webContents.setZoomLevel(0); e.preventDefault(); }
  });
}

app.on('second-instance', () => { if (win) { if (win.isMinimized()) win.restore(); win.focus(); } });
app.whenReady().then(createWindow);
app.on('window-all-closed', () => app.quit());
