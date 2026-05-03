const STATUS = {
  PENDING: {
    icon: (<div className="w-6 h-6 rounded-full border-2 border-gray-300 bg-white"></div>),
    textColor: "text-gray-400"
  },
  ACTIVE: {
    icon: (
      <div className="relative w-6 h-6 rounded-full border-2 border-brand-600 bg-white flex items-center justify-center">
        <div className="absolute inset-0 rounded-full bg-brand-100 animate-pulse-soft"></div>
        <div className="relative w-2 h-2 bg-brand-600 rounded-full"></div>
      </div>
    ),
    textColor: "text-brand-600 font-bold"
  },
  DONE: {
    icon: (
      <div className="w-6 h-6 rounded-full bg-green-500 flex items-center justify-center text-white shadow-sm">
        <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" strokeWidth="3" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
        </svg>
      </div>
    ),
    textColor: "text-gray-900 font-semibold"
  },
  FAILED: {
    icon: (
      <div className="w-6 h-6 rounded-full bg-red-500 flex items-center justify-center text-white shadow-sm">
        <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" strokeWidth="3" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </div>
    ),
    textColor: "text-red-600 font-bold"
  },
  SKIPPED: {
    icon: (<div className="w-6 h-6 rounded-full border-2 border-dashed border-gray-200 bg-white"></div>),
    textColor: "text-gray-300"
  },
};

export default function SagaStep({ label, status, detail, isLast }) {
  const s = STATUS[status] || STATUS.PENDING;
  return (
    <div className="relative flex items-start gap-4 pb-5 last:pb-0">
      {!isLast && (
        <div className={`absolute left-3 top-7 w-0.5 h-full ${
          status === "DONE" ? "bg-green-300" :
          status === "FAILED" ? "bg-red-200" :
          "bg-gray-200"
        }`}></div>
      )}
      <div className="relative z-10 flex-shrink-0 mt-0.5">{s.icon}</div>
      <div className="flex-1 min-w-0">
        <div className={`text-sm ${s.textColor}`}>{label}</div>
        {detail && <div className="text-xs text-gray-500 mt-0.5">{detail}</div>}
      </div>
    </div>
  );
}
