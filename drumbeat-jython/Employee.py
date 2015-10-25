# Jython source file
from fi.hut.cs.drumbeat.jython import EmployeeType

class Employee(EmployeeType):
   def __init__(self):
      self.first = "Josh"
      self.last  = "Juneau"
      self.id = "myempid"

   def getEmployeeFirst(self):
      return self.first

   def getEmployeeLast(self):
      return self.last

   def getEmployeeId(self):
      return self.id