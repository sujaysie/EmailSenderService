import { useEffect, useMemo, useState } from 'react';

const hours = ['00', '02', '04', '06', '08', '10', '12', '14', '16', '18', '20', '22'];
const sendData = [42, 38, 61, 55, 70, 88, 120, 145, 132, 160, 178, 155];
const deliverData = [40, 36, 58, 52, 67, 84, 116, 140, 128, 154, 172, 149];
const maxVal = Math.max(...sendData);

const templates = [
  { name: 'Welcome Email', cat: 'Transactional', icon: '👋', ver: 'v4', vars: 5, bg: 'rgba(59,230,168,0.10)' },
  { name: 'Password Reset', cat: 'Security', icon: '🔐', ver: 'v2', vars: 3, bg: 'rgba(99,190,255,0.10)' },
  { name: 'Order Confirmed', cat: 'Commerce', icon: '📦', ver: 'v6', vars: 8, bg: 'rgba(245,166,35,0.10)' },
  { name: 'Weekly Digest', cat: 'Marketing', icon: '📰', ver: 'v1', vars: 12, bg: 'rgba(255,77,106,0.10)' },
  { name: 'Invoice Ready', cat: 'Billing', icon: '💳', ver: 'v3', vars: 7, bg: 'rgba(59,230,168,0.10)' },
];

const topics = [
  { name: 'email.send.requests', parts: 12, replicas: 3, lag: 0, rps: 214, status: 'healthy' },
  { name: 'email.send.retry', parts: 6, replicas: 3, lag: 3, rps: 12, status: 'healthy' },
  { name: 'email.send.high', parts: 4, replicas: 3, lag: 0, rps: 31, status: 'healthy' },
  { name: 'email.delivery.status', parts: 6, replicas: 3, lag: 0, rps: 198, status: 'healthy' },
  { name: 'email.send.dlq', parts: 3, replicas: 3, lag: 7, rps: 2, status: 'warn' },
];

const consumers = [
  { id: 'engine-pod-01', load: 78, msgs: '1.2k/s', lag: 0 },
  { id: 'engine-pod-02', load: 65, msgs: '0.9k/s', lag: 0 },
  { id: 'engine-pod-03', load: 81, msgs: '1.3k/s', lag: 1 },
  { id: 'engine-pod-04', load: 44, msgs: '0.6k/s', lag: 0 },
  { id: 'engine-pod-05', load: 70, msgs: '1.1k/s', lag: 0 },
  { id: 'engine-pod-06', load: 55, msgs: '0.8k/s', lag: 0 },
];

const logs = [
  { time: '14:22:08', type: 'success', title: 'Batch delivered — 500 msgs', desc: 'correlationId: a3f2-bc91 · template: order_confirmed' },
  { time: '14:21:55', type: 'info', title: 'Consumer rebalance completed', desc: 'engine-engine-group · partitions reassigned' },
  { time: '14:21:31', type: 'success', title: 'Template published — welcome_v4', desc: 'version_number: 4 · activated immediately' },
  { time: '14:20:44', type: 'warn', title: 'Retry scheduled — eventId: 9d14', desc: 'attempt: 2 · backoff: 4s · cause: SMTP timeout' },
  { time: '14:19:12', type: 'success', title: 'Outbox relay — 87 msgs flushed', desc: 'kafka lag: 0 · relay latency: 12ms' },
  { time: '14:18:05', type: 'error', title: 'DLQ — permanent bounce', desc: 'eventId: 2c88 · reason: 550 mailbox not found' },
];

const nav = [
  { label: 'Overview', icon: '⬡', id: 'overview' },
  { label: 'Send Mail', icon: '✉', id: 'send' },
  { label: 'Templates', icon: '◫', id: 'templates', badge: '5' },
  { label: 'Kafka Topics', icon: '⇌', id: 'kafka' },
  { label: 'Engine', icon: '⚙', id: 'engine' },
  { label: 'Event Log', icon: '≡', id: 'logs', badge: '7' },
];

const sectionTitles = {
  overview: 'Overview',
  send: 'Send Mail',
  templates: 'Templates',
  kafka: 'Kafka Topics',
  engine: 'Email Engine',
  logs: 'Event Log',
};

function OverviewSection() {
  return (
    <>
      <div className="stats-row">
        {[
          { label: 'Sent (24h)', value: '2.41M', delta: '+12.4%', dir: 'up', icon: '✉' },
          { label: 'Delivered', value: '99.3%', delta: '+0.1%', dir: 'up', icon: '✓', cls: 'blue' },
          { label: 'Avg Latency', value: '148ms', delta: '-8ms', dir: 'up', icon: '⚡', cls: 'orange' },
          { label: 'DLQ Events', value: '14', delta: '+3', dir: 'down', icon: '⚠', cls: 'red' },
        ].map((s, i) => (
          <div key={s.label} className={`stat-card ${s.cls || ''} fade-up fade-up-${i + 1}`}>
            <div className="stat-icon">{s.icon}</div>
            <div className="stat-label">{s.label}</div>
            <div className="stat-value">{s.value}</div>
            <div className={`stat-delta ${s.dir}`}>{s.delta} vs yesterday</div>
          </div>
        ))}
      </div>

      <div className="two-col three fade-up fade-up-2">
        <div className="card">
          <div className="card-header">
            <span className="card-title">Throughput — last 24h</span>
            <span className="card-meta">msgs/min (×1000)</span>
          </div>
          <div className="card-body">
            <div className="chart-legend">
              <div className="legend-item"><div className="legend-dot accent2" />Sent</div>
              <div className="legend-item"><div className="legend-dot accent" />Delivered</div>
            </div>
            {[sendData, deliverData].map((series, idx) => (
              <div className="chart-area" key={idx}>
                <div className="chart-bars">
                  {series.map((value, i) => (
                    <div key={`${idx}-${hours[i]}`} className="chart-bar-wrap">
                      <div className={`chart-bar ${idx === 1 ? 'success' : ''}`} style={{ height: `${(value / maxVal) * 100}%` }} title={`${value}k ${idx === 0 ? 'sent' : 'delivered'}`} />
                      <div className="chart-x" style={idx === 1 ? { opacity: 0 } : undefined}>{hours[i]}</div>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <span className="card-title">Event Log</span>
            <span className="card-meta">live</span>
          </div>
          <div className="card-body event-log">
            {logs.slice(0, 4).map((log) => (
              <LogRow key={`${log.time}-${log.title}`} log={log} />
            ))}
          </div>
        </div>
      </div>

      <div className="card fade-up fade-up-3">
        <div className="card-header">
          <span className="card-title">Platform Architecture</span>
          <span className="card-meta">data flow</span>
        </div>
        <div className="kafka-flow">
          {[
            { label: 'REST / CLI', sub: 'Mail Gateway', icon: '🌐', cls: 'active' },
            null,
            { label: 'Outbox', sub: 'PostgreSQL', icon: '🗄', cls: '' },
            null,
            { label: 'Kafka', sub: 'email.send.requests', icon: '⚡', cls: 'kafka' },
            null,
            { label: 'Email Engine', sub: 'Consumer Group', icon: '⚙', cls: 'active' },
            null,
            { label: 'Template Svc', sub: 'Redis Cache', icon: '📋', cls: '' },
            null,
            { label: 'SMTP / SES', sub: 'Mail Provider', icon: '✉', cls: 'active' },
          ].map((node, i) => node ? (
            <div key={i} className="kf-node">
              <div className={`kf-box ${node.cls}`}>
                <div className="kf-icon">{node.icon}</div>
                <div className="kf-name">{node.label}</div>
                <div className="kf-sub">{node.sub}</div>
              </div>
            </div>
          ) : (
            <div key={i} className="kf-arrow">
              <div className="kf-line" />
              <div className="kf-topic">{i === 1 ? 'outbox relay' : i === 3 ? 'produce' : i === 5 ? 'consume' : i === 7 ? 'render' : 'dispatch'}</div>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}

function SendSection() {
  const [priority, setPriority] = useState('NORMAL');
  const [sent, setSent] = useState(false);
  const [eventId, setEventId] = useState('');

  const queueEmail = () => {
    setEventId(Math.random().toString(36).slice(2, 10).toUpperCase());
    setSent(true);
  };

  return (
    <div className="send-layout">
      <div className="card fade-up">
        <div className="card-header">
          <span className="card-title">Send Email</span>
          <span className="card-meta">publishes to email.send.requests</span>
        </div>
        <div className="card-body">
          {sent ? (
            <div className="result-state">
              <div className="result-icon">✅</div>
              <div className="result-title">Queued Successfully</div>
              <div className="result-meta">eventId: {eventId} · status: QUEUED</div>
              <button className="topbar-btn btn-ghost" onClick={() => setSent(false)}>Send Another</button>
            </div>
          ) : (
            <div className="send-form">
              <div className="form-row">
                <div className="field">
                  <label>Template</label>
                  <select>{templates.map((template) => <option key={template.name}>{template.name}</option>)}</select>
                </div>
                <div className="field">
                  <label>Priority</label>
                  <select value={priority} onChange={(e) => setPriority(e.target.value)}>
                    <option>HIGH</option>
                    <option>NORMAL</option>
                    <option>LOW</option>
                  </select>
                </div>
              </div>
              <div className="field">
                <label>Recipients (comma-separated)</label>
                <input type="text" placeholder="alice@example.com, bob@example.com" />
              </div>
              <div className="form-row">
                <div className="field">
                  <label>CC</label>
                  <input type="text" placeholder="Optional" />
                </div>
                <div className="field">
                  <label>Reply-To</label>
                  <input type="text" placeholder="noreply@company.com" />
                </div>
              </div>
              <div className="field">
                <label>Template Variables (JSON)</label>
                <textarea placeholder='{"firstName":"Alice","orderId":"ORD-1234"}' />
              </div>
              <div className="form-row">
                <div className="field">
                  <label>Correlation ID</label>
                  <input type="text" placeholder="auto-generated if empty" />
                </div>
                <div className="field">
                  <label>Schedule At (optional)</label>
                  <input type="datetime-local" />
                </div>
              </div>
              <div className="action-row">
                <button className="topbar-btn btn-primary fill" onClick={queueEmail}>✉ Queue Send</button>
                <button className="topbar-btn btn-ghost">Preview</button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

function TemplatesSection() {
  const [selected, setSelected] = useState(templates[0]);

  return (
    <div className="two-col fade-up">
      <div className="card">
        <div className="card-header">
          <span className="card-title">Templates</span>
          <button className="topbar-btn btn-primary compact">+ New</button>
        </div>
        <div className="card-body template-list">
          {templates.map((template) => (
            <div key={template.name} className={`template-item ${selected.name === template.name ? 'selected' : ''}`} onClick={() => setSelected(template)}>
              <div className="template-icon" style={{ background: template.bg }}>{template.icon}</div>
              <div className="template-info">
                <div className="template-name">{template.name}</div>
                <div className="template-meta">{template.cat} · {template.vars} variables</div>
              </div>
              <div className="template-ver">{template.ver}</div>
            </div>
          ))}
        </div>
      </div>
      <div className="card">
        <div className="card-header">
          <span className="card-title">{selected.icon} {selected.name}</span>
          <span className="pill blue">{selected.ver}</span>
        </div>
        <div className="tabs">
          <div className="tab active">HTML</div>
          <div className="tab">Text</div>
          <div className="tab">Variables</div>
          <div className="tab">History</div>
        </div>
        <div className="card-body">
          <pre className="code-block">{`<!DOCTYPE html>
<html>
  <head><meta charset="utf-8"></head>
  <body>
    <h1>Hello, [[${'{firstName}'}]]!</h1>
    <p>Your template is ready. Category: ${selected.cat}</p>
    <!-- ${selected.vars} declared variables -->
  </body>
</html>`}</pre>
          <div className="action-row wrap">
            <button className="topbar-btn btn-primary compact">Publish Version</button>
            <button className="topbar-btn btn-ghost compact">Activate</button>
            <button className="topbar-btn btn-ghost compact">Render Preview</button>
          </div>
        </div>
      </div>
    </div>
  );
}

function KafkaSection() {
  return (
    <div className="card fade-up">
      <div className="card-header">
        <span className="card-title">Kafka Topics</span>
        <span className="card-meta">broker: kafka-cluster-01:9092</span>
      </div>
      <table className="topic-table">
        <thead>
          <tr>
            <th>Topic Name</th>
            <th>Partitions</th>
            <th>Replicas</th>
            <th>Consumer Lag</th>
            <th>Throughput</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {topics.map((topic) => (
            <tr key={topic.name}>
              <td className="mono accent2">{topic.name}</td>
              <td className="mono">{topic.parts}</td>
              <td className="mono">{topic.replicas}</td>
              <td className={`mono ${topic.lag > 0 ? 'accent3' : 'accent'}`}>{topic.lag}</td>
              <td className="mono">{topic.rps} msg/s</td>
              <td><span className={`pill ${topic.status === 'healthy' ? 'green' : 'orange'}`}>{topic.status}</span></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function EngineSection() {
  return (
    <>
      <div className="stats-row stats-row-3 fade-up">
        {[
          { label: 'Active Consumers', value: '6 / 12', icon: '⚙' },
          { label: 'Messages/sec', value: '5.9k', icon: '⚡' },
          { label: 'Retry Queue', value: '3', icon: '↻' },
        ].map((card) => (
          <div key={card.label} className="stat-card">
            <div className="stat-icon">{card.icon}</div>
            <div className="stat-label">{card.label}</div>
            <div className="stat-value small">{card.value}</div>
          </div>
        ))}
      </div>
      <div className="card fade-up">
        <div className="card-header">
          <span className="card-title">Consumer Pods — email-engine-group</span>
          <span className="pill green">All Healthy</span>
        </div>
        <div className="card-body">
          <div className="consumer-grid">
            {consumers.map((consumer) => (
              <div key={consumer.id} className="consumer-card">
                <div className="consumer-header">
                  <div className="consumer-id">{consumer.id}</div>
                  <span className={`pill ${consumer.lag > 0 ? 'orange' : 'green'}`}>{consumer.lag > 0 ? `lag:${consumer.lag}` : 'ok'}</span>
                </div>
                <div className="consumer-bar-wrap"><div className="consumer-bar" style={{ width: `${consumer.load}%` }} /></div>
                <div className="consumer-stats">
                  <div className="cs-item"><div className="cs-label">load</div><div className="cs-value">{consumer.load}%</div></div>
                  <div className="cs-item"><div className="cs-label">msgs</div><div className="cs-value">{consumer.msgs}</div></div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </>
  );
}

function LogsSection() {
  return (
    <div className="card fade-up">
      <div className="card-header">
        <span className="card-title">Event Log</span>
        <div className="pill-group">
          <span className="pill green">success</span>
          <span className="pill blue">info</span>
          <span className="pill orange">warn</span>
          <span className="pill red">error</span>
        </div>
      </div>
      <div className="card-body event-log">
        {[...logs, ...logs.slice(0, 3)].map((log, index) => <LogRow key={`${index}-${log.time}`} log={log} />)}
      </div>
    </div>
  );
}

function LogRow({ log }) {
  return (
    <div className="log-item">
      <div className={`log-dot ${log.type}`} />
      <div className="log-content">
        <div className="log-title">{log.title}</div>
        <div className="log-desc">{log.desc}</div>
      </div>
      <div className="log-time">{log.time}</div>
    </div>
  );
}

export default function App() {
  const [active, setActive] = useState('overview');
  const [tick, setTick] = useState(0);

  useEffect(() => {
    const id = window.setInterval(() => setTick((value) => value + 1), 2200);
    return () => window.clearInterval(id);
  }, []);

  const tickerMessages = useMemo(() => ([
    <> <span>engine-pod-03</span> delivered 142 msgs · lag: 0 </>,
    <> <span>outbox relay</span> flushed 87 rows → Kafka in 11ms </>,
    <> <span>email.send.requests</span> throughput: 214 msg/s </>,
    <> <span>template:welcome_v4</span> cache hit · rendered in 3ms </>,
    <> <span>HIGH-priority</span> queue: 0 msgs pending </>,
  ]), []);

  return (
    <>
      <div className="grid-bg" />
      <div className="app-shell">
        <aside className="sidebar">
          <div className="sidebar-logo">
            <div className="logo-mark">Mail<span>Flow</span></div>
            <div className="logo-sub">v2.4.1 · kafka</div>
          </div>
          <div className="nav-section">
            <div className="nav-label">Platform</div>
            {nav.map((item) => (
              <button key={item.id} className={`nav-item ${active === item.id ? 'active' : ''}`} onClick={() => setActive(item.id)}>
                <span className="icon">{item.icon}</span>
                <span>{item.label}</span>
                {item.badge ? <span className={`nav-badge ${item.id === 'logs' ? 'alert' : ''}`}>{item.badge}</span> : null}
              </button>
            ))}
          </div>
          <div className="sidebar-footer">
            <div className="status-line"><span className="status-dot" />All systems operational</div>
          </div>
        </aside>

        <main className="main">
          <div className="topbar">
            <div className="page-title">{sectionTitles[active]} <span>/ MailFlow Platform</span></div>
            <button className="topbar-btn btn-ghost">⟳ Refresh</button>
            <button className="topbar-btn btn-ghost">⚙ Config</button>
            <button className="topbar-btn btn-primary" onClick={() => setActive('send')}>+ Send Mail</button>
          </div>

          <div className="live-row">
            <span className="live-badge">LIVE</span>
            <span className="ticker-item">{tickerMessages[tick % tickerMessages.length]}</span>
            <span className="sep">·</span>
            <span className="uptime">uptime: 14d 7h 22m</span>
          </div>

          <div className="content">
            {active === 'overview' && <OverviewSection />}
            {active === 'send' && <SendSection />}
            {active === 'templates' && <TemplatesSection />}
            {active === 'kafka' && <KafkaSection />}
            {active === 'engine' && <EngineSection />}
            {active === 'logs' && <LogsSection />}
          </div>
        </main>
      </div>
    </>
  );
}
