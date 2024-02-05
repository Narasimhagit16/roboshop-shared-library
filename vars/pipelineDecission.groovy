#!groovy
def decidePipeline(Map configMap){
    application = configMap.get("application")
    component = configMap.get("component")
    switch(application) {
        case 'nodejsVM':
             nodejsVM(configMap)
             break
        case 'javaVM':
             javaVM(configMap)
             break
        case 'pythonjsVM':
             pythonVM(configMap)
             break
        case 'goVM':
             goVM(configMap)
             break
        default:
             error "application is not recongized"
             break
    }






}