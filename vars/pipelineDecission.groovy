#!groovy
def decidePipeline(Map configMap){
    application = configMap('application')
    component = configMap('component')
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
             error('application is not recongized')
             break
    }






}