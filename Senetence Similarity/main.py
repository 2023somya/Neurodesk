from fastapi import FastAPI, Request
from sentence_transformers import SentenceTransformer, util
from typing import List
from pydantic import BaseModel

app = FastAPI()
model = SentenceTransformer("all-MiniLM-L6-v2")

class TicketData(BaseModel):
    new_ticket : str
    past_tickets: List[str]
    
@app.post("/similarity")
async def get_similarity(data: TicketData):
    print("Received payload:", data)

    if not data.past_tickets:
        return {"scores": []}
    new_embedding = model.encode(data.new_ticket, convert_to_tensor=True)
    past_embeddings =     model.encode(data.past_tickets, convert_to_tensor=True)
    scores = util.cos_sim(new_embedding, past_embeddings)[0]
    return {
        "scores": [round(float(score), 4) for score in scores]
    }
#all mp net base v2
#granite 2.3 vision - image processir
#
#rag for summary-> 