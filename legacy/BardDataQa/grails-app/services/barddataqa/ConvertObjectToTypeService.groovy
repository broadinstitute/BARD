package barddataqa

class ConvertObjectToTypeService {

    public <T> List<T> convert(List<Object> objectList) {
        List<T> result = new ArrayList<T>(objectList.size())

        for (Object object : objectList) {
            result.add((T)object)
        }

        return result
    }
}
