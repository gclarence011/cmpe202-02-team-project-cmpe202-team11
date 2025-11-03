export default function ChatBubble({ me, text, time }:{ me?:boolean; text:string; time?:string; }){
  return (
    <div className={`flex ${me?"justify-end":""}`}>
      <div className={`max-w-[70%] rounded-2xl px-3 py-2 my-1 ${me?"bg-black text-white":"bg-gray-100"}`}>
        <div className="text-sm">{text}</div>
        {time && <div className="text-[10px] opacity-70 mt-1">{time}</div>}
      </div>
    </div>
  );
}
