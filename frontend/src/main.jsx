import React, { useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'
import { MapContainer, TileLayer, Polygon, Polyline, CircleMarker, Tooltip, useMap } from 'react-leaflet'
import 'leaflet/dist/leaflet.css'
import './style.css'

const colors = { HIGH: '#ef5b55', MEDIUM: '#e9a44a', LOW: '#4bc5aa' }
const labels = { HIGH: '고위험', MEDIUM: '주의', LOW: '정상' }

function Focus({ vessel }) {
  const map = useMap()
  useEffect(() => { if (vessel) map.flyTo([vessel.latest.position.latitude, vessel.latest.position.longitude], 9, { duration: 0.7 }) }, [map, vessel])
  return null
}

function App() {
  const [vessels, setVessels] = useState([])
  const [areas, setAreas] = useState([])
  const [selected, setSelected] = useState(null)
  const [error, setError] = useState('')
  useEffect(() => {
    Promise.all([fetch('/api/vessels'), fetch('/api/restricted-areas')])
      .then(async ([v, a]) => { if (!v.ok || !a.ok) throw Error('API 응답 오류'); return Promise.all([v.json(), a.json()]) })
      .then(([v, a]) => { setVessels(v); setAreas(a); setSelected(v.find(x => x.risk.level === 'HIGH')?.mmsi ?? v[0]?.mmsi) })
      .catch(() => setError('서버에 연결할 수 없습니다. Spring Boot API가 실행 중인지 확인하세요.'))
  }, [])
  const vessel = vessels.find(v => v.mmsi === selected)
  const highCount = vessels.filter(v => v.risk.level === 'HIGH').length
  return <div className="app">
    <aside className="sidebar">
      <header><div className="brand-mark">◈</div><div><strong>해역 감시 대시보드</strong><small>FISHING RISK MONITOR</small></div></header>
      <div className="intro"><span className="eyebrow">DEMO / MOCK DATA</span><h1>불법조업 의심<br/>선박 탐지</h1><p>제한 해역과 선박 항적을 바탕으로 위험 신호를 확인합니다.</p></div>
      <div className="stats"><div><b>{vessels.length}</b><span>관찰 선박</span></div><div><b className="danger">{highCount}</b><span>고위험 경고</span></div></div>
      <div className="section-title">선박 목록 <span>{vessels.length}척</span></div>
      <div className="vessel-list">{vessels.map(v => <button key={v.mmsi} className={'vessel-row ' + (selected === v.mmsi ? 'active' : '')} onClick={() => setSelected(v.mmsi)}><span className="row-dot" style={{background:colors[v.risk.level]}}/><span className="row-name"><strong>{v.name}</strong><small>MMSI {v.mmsi}</small></span><span className={'badge ' + v.risk.level.toLowerCase()}>{labels[v.risk.level]}</span></button>)}</div>
      {error && <div className="error">{error}</div>}
      <footer>규칙 기반 위험 추정 · 실제 불법 여부를 확정하지 않습니다.</footer>
    </aside>
    <main className="map-wrap"><MapContainer center={[34.47,125.93]} zoom={9} zoomControl={false} className="map"><TileLayer attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors' url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
      {areas.map(a => <Polygon key={a.id} positions={a.polygon.map(p => [p.latitude,p.longitude])} pathOptions={{color:'#e98864', weight:2, fillColor:'#ec825f', fillOpacity:0.16, dashArray:'6 5'}}><Tooltip sticky>{a.name}</Tooltip></Polygon>)}
      {vessels.map(v => <React.Fragment key={v.mmsi}><Polyline positions={v.track.map(p => [p.position.latitude,p.position.longitude])} pathOptions={{color:colors[v.risk.level], weight:selected===v.mmsi?4:2, opacity:selected===v.mmsi?0.9:0.5}}/><CircleMarker center={[v.latest.position.latitude,v.latest.position.longitude]} radius={selected===v.mmsi?11:8} pathOptions={{color:'#fff',weight:2,fillColor:colors[v.risk.level],fillOpacity:1}} eventHandlers={{click:()=>setSelected(v.mmsi)}}><Tooltip direction="top" offset={[0,-8]}>{v.name} · 위험도 {v.risk.score}</Tooltip></CircleMarker></React.Fragment>)}
      <Focus vessel={vessel}/></MapContainer>
      <div className="map-label"><span className="pulse"/> 실시간 화면 예시 <em>MOCK DATA</em></div>
      <div className="legend"><strong>지도 범례</strong><span><i className="legend-area"/> 제한 해역</span><span><i style={{background:colors.HIGH}}/> 고위험</span><span><i style={{background:colors.MEDIUM}}/> 주의</span><span><i style={{background:colors.LOW}}/> 정상</span></div>
      {vessel && <section className="detail"><div className="detail-head"><div><span className="eyebrow">VESSEL DETAILS</span><h2>{vessel.name}</h2><p>MMSI {vessel.mmsi}</p></div><span className={'badge large ' + vessel.risk.level.toLowerCase()}>{labels[vessel.risk.level]}</span></div>
        <div className="risk-block"><div><span>위험 점수</span><strong>{vessel.risk.score}<small> / 100</small></strong></div><div className="bar"><i style={{width:vessel.risk.score+'%', background:colors[vessel.risk.level]}}/></div></div>
        <div className="facts"><div><span>현재 속력</span><strong>{vessel.latest.speedKnots.toFixed(1)} kn</strong></div><div><span>조업 의심</span><strong>{vessel.risk.fishingSuspected?'감지':'미감지'}</strong></div><div><span>제한 해역</span><strong>{vessel.risk.insideRestrictedArea?'내부':'외부'}</strong></div><div><span>마지막 기록</span><strong>{new Date(vessel.latest.time).toLocaleString('ko-KR',{timeZone:'Asia/Seoul',month:'numeric',day:'numeric',hour:'2-digit',minute:'2-digit'})}</strong></div></div>
        <div className="reasons"><strong>위험 판단 근거</strong>{vessel.risk.reasons.map((reason,i)=><p key={i}><span>!</span>{reason}</p>)}</div>
      </section>}
    </main>
  </div>
}

createRoot(document.getElementById('root')).render(<App />)
