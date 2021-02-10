package ru.skillbox.socialnetwork.dto.universal;

import java.util.ArrayList;
import java.util.List;

public class ResponseFactory {public static BaseResponse getBaseResponse(Dto dto){
    return new BaseResponse(dto);
}

    public static BaseResponse responseOk(){
        return new BaseResponse(new MessageResponseDto("ok"));
    }

    public static BaseResponseList getBaseResponseList(List<Dto> list, long total, long offset, long limit){
        return new BaseResponseList(
                total,
                offset,
                limit,
                list
        );
    }

    public static BaseResponseList getBaseResponseListWithLimit(List<Dto> list, long offset, long limit){
        List<Dto> data = getElementsInRange(list, offset, limit);
        return new BaseResponseList(
                list.size(),
                offset,
                limit,
                data
        );
    }

    public static ErrorResponse getErrorResponse(String error, String errorDescription){
        return new ErrorResponse(error, errorDescription);
    }


    private static List<Dto> getElementsInRange(List<Dto> list, long offset, long limit) {
        int lastElementIndex = (int) (offset + limit);
        int lastPostIndex = list.size();
        if (lastPostIndex >= offset) {//если есть элементы входящие в нужный диапазон
            if (lastElementIndex <= lastPostIndex) {//если все элементы с нужными индексами есть в листе
                return list.subList((int) offset, lastElementIndex);
            } else {//если не хватает элементов, то в посты записываем остаток, считая от offset
                return list.subList((int) offset, lastPostIndex);
            }
        } else {
            return new ArrayList<>();

        }
    }

}
