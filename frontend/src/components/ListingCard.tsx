import { Link } from "react-router-dom";
import placeholder from "../assets/default-item.png";

type Props = {
  id: string;
  title: string;
  price: number;
  thumbnailUrl?: string;
  badge?: string;
};

export default function ListingCard({ id, title, price, thumbnailUrl, badge }: Props) {
  return (
    <Link
      to={`/listings/${id}`}
      className="group relative block rounded-2xl overflow-hidden bg-white border shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-200"
    >
      <div className="aspect-[4/3] bg-gray-100 overflow-hidden">
        <img
          src={thumbnailUrl || placeholder}
          alt={title}
          loading="lazy"
          className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
          onError={(e) => { (e.currentTarget as HTMLImageElement).src = placeholder; }}
        />
      </div>

      {badge && (
        <span className="absolute left-3 top-3 rounded-full bg-black/80 text-white text-xs px-2 py-1">
          {badge}
        </span>
      )}

      <div className="p-3">
        <div className="flex items-start justify-between gap-2">
          <h3 className="font-semibold leading-tight line-clamp-2">{title}</h3>
          <span className="shrink-0 rounded-lg bg-gray-900 text-white px-2 py-1 text-sm">
            ${price.toFixed(2)}
          </span>
        </div>
        <div className="mt-2 text-xs text-gray-500">Tap to view details</div>
      </div>
    </Link>
  );
}
