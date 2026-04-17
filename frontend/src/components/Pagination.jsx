export default function Pagination({ page, totalPages, onPrev, onNext }) {
  return (
    <div className="pagination-row">
      <button type="button" className="btn secondary" onClick={onPrev} disabled={page <= 0}>
        Previous
      </button>
      <span>
        Page {page + 1} of {Math.max(totalPages, 1)}
      </span>
      <button
        type="button"
        className="btn secondary"
        onClick={onNext}
        disabled={page + 1 >= totalPages}
      >
        Next
      </button>
    </div>
  );
}
